/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import au.com.bytecode.opencsv.CSVReader;
import com.oreilly.servlet.MultipartRequest;
import dbServices.DB;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author DLK-F2
 */
public class Data extends HttpServlet {

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
       ServletContext sc=request.getSession().getServletContext();
        MultipartRequest m=new MultipartRequest(request,sc.getRealPath("dataset"));
            File file=m.getFile("file");
            String fname=file.getName();
            String csvFile =sc.getRealPath("dataset")+"\\"+fname;
            CSVReader reader = null;
            Connection con=new DB().Con();
            try
            {
                PreparedStatement paa= con.prepareStatement("truncate table dataset");
                paa.executeUpdate();
            
                reader = new CSVReader(new FileReader(csvFile));
                String[] line;
                while ((line = reader.readNext()) != null)
                {
                    
                     String query="insert into dataset(id,uname,dte,tweet)values('"+line[0]+"','"+line[1]+"','"+line[2]+"','"+line[3]+"') ";
                     PreparedStatement ps=con.prepareStatement(query);
                    System.out.println(query);
                    ps.executeUpdate();
                    System.out.println(query);
                }
                out.println("<script type=\"text/javascript\">"); 			
		out.println("alert(\"Succesfully Updated for sql\")");
		out.println("</script>");
		RequestDispatcher rd=request.getRequestDispatcher("Collection.jsp");
		rd.include(request,response);
            } catch (IOException | SQLException e) 
            {
                System.out.println(e);
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
