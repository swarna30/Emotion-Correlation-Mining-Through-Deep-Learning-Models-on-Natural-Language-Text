/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import CBF.Stem;
import CBF.Stopwords;
import CBF.replace;
import dbServices.DB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pro.Classifer;

/**
 *
 * @author DLK-F2
 */
public class Analysis extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
       PrintWriter out = response.getWriter();
        {
            
            try {
            String feed="",status="";
            Connection con=new DB().Con();
            Classifer cls = new Classifer();
            Map <String, String> status1;
            Set<String> setob;
            Collection col;
            String sta;
            String col1; 
            Stopwords st=new Stopwords();
            Stem stem=new Stem();
            replace rep=new replace();
            String tweets="",twitter_id="",user_id="",id="";
                PreparedStatement p2 = con.prepareStatement("truncate table output");
                     p2.executeUpdate();
                PreparedStatement pss124 = con.prepareStatement("select * from dataset ");
                ResultSet rs = pss124.executeQuery();               
                while(rs.next())
                {
                     id=rs.getString("id");
                     twitter_id=rs.getString("uname");
                     user_id=rs.getString("dte");
                     tweets=rs.getString("tweet");
                    tweets=rep.remove(tweets);
                    tweets=st.words(tweets);
                    tweets=stem.stem(tweets); 
                    status1 = cls.data(tweets);
                    setob = status1.keySet();
                    col = status1.values();
                    sta = setob.toString();
                    col1 = col.toString();
                    sta=sta.replaceAll("[^a-zA-Z0-9]", "");
                    col1=col1.replaceAll("[^a-zA-Z0-9]", "");

                    tweets = tweets.toLowerCase();
                    System.out.println("tweet: "+tweets);
                    String[] arr = tweets.split(" "); 
                    int cinema=0,sports=0,news=0;String keyword="";
                    int count=0;
                    String nstatus="";
                     for ( String ss : arr)
                     {
                     PreparedStatement pss =con.prepareStatement("select * from cinema where word='"+ss+"' ");
                     ResultSet rss = pss.executeQuery();
                     System.out.println(pss);
                     while(rss.next())
                     {
                     cinema=cinema+1;
                     keyword+=rss.getString("word")+" ";
                     System.out.println("Title :  "+ss);
                     }                     
                     PreparedStatement pss1 = con.prepareStatement("select * from sports where word='"+ss+"' ");
                     ResultSet rss1 = pss1.executeQuery();
                     System.out.println(pss1);
                     while(rss1.next())
                     {
                     sports=sports+1;
                     keyword+=rss1.getString("word")+" ";
                     System.out.println("Emotion : "+ss);
                     }
                     PreparedStatement pss2 = con.prepareStatement("select * from news where word='"+ss+"' ");
                     ResultSet rss2 = pss2.executeQuery();
                     System.out.println(pss2);
                     while(rss2.next())
                     {
                     news=news+1;
                     keyword+=rss2.getString("word")+" ";
                     System.out.println("Comment : "+ss);
                     }
                     
                     }   
                     if(cinema!=0)
                     {
                     nstatus+=" Title : "+cinema;
                     }
                     if(sports!=0)
                     {
                     nstatus+=", Emotion : "+sports;
                     }
                     if(news!=0)
                     {
                     nstatus+=", Comment : "+news;
                     }
                     
                     if((cinema>sports)&&(cinema>news))
                     {
                     status="Title";
                     }
                     else if((sports>cinema)&&(sports>news))
                     {
                     status="Emotion";
                     }
                     else if((news>cinema)&&(news>sports))
                     {
                     status="Comment";
                     }
                     
                     else if((cinema==0)&&(sports==0)&&(news==0))
                     {
                     status="Others";
                     }
                     else
                     {
                     status="Equal";                     
                     }
                     System.out.println("values of nstatus "+nstatus);
                     nstatus=nstatus.trim();
                     String finstatus ="";
                     try
                     {
                         finstatus = nstatus.substring(0,nstatus.length());
                         System.out.println("final status: "+finstatus);
                     }
                     catch(Exception ex)
                     {
                         
                     }
                     
                     System.out.println("-----------");                
                     System.out.println("Title: "+cinema);
                     System.out.println("Emotion: "+sports);
                     System.out.println("Comment: "+news);
                     
                     PreparedStatement p = con.prepareStatement("update dataset set status='"+status+"',nstatus='"+finstatus+"',cinema='"+cinema+"',sports='"+sports+"',news='"+news+"' where id='"+id+"' ");
                     p.executeUpdate();
                     
                     PreparedStatement p1 = con.prepareStatement("insert into output(id,uname,dte,tweet,status,nstatus,cinema,sports,news,keyword)values('"+id+"','"+twitter_id+"','"+user_id+"','"+tweets+"','"+status+"','"+nstatus+"','"+cinema+"','"+sports+"','"+news+"','"+keyword+"') ");
                     p1.executeUpdate();
                }                
                System.out.println("all process completed.");       
                out.println("<script type=\"text/javascript\">"); 			
		out.println("alert(\"Analysis Completed...\")");
		out.println("</script>");
		RequestDispatcher rd=request.getRequestDispatcher("Hashtag.jsp");
		rd.include(request,response);
                
            
            } catch (Exception ex)
            {
               
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
