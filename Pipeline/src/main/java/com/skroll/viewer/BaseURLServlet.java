package com.skroll.viewer;

import com.aliasi.util.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Created by saurabh on 5/17/15.
 */
public class BaseURLServlet extends HttpServlet{

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        File file = new File("src/main/webapp/index.html");
        response.getWriter().write(Files.readFromFile(file, "UTF8"));
    }
}
