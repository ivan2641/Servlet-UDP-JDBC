/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Web;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.SocketException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author vanab
 */
public class ExamWeb extends HttpServlet {

    ExamUDPClient udpClient;
    String[] noteList;
    String path;

    @Override
    public void init() {
        path = "/index.html";
        log("Servlet ExamWeb created..............................");
    }

    @Override
    public void destroy() {
        log("Servlet ExamWeb deleted...............................");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String user = request.getParameter("user");
        Map<String, String[]> map = request.getParameterMap();
        if (request.getParameter("Logout") != null) {
            path = "/index.html";
            RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
            requestDispatcher.forward(request, response);
        }
        HttpSession session = request.getSession();
        if (session.isNew()) {
            path = "/index.html";
            udpClient = new ExamUDPClient();
            session.getServletContext().setAttribute("udpClient", udpClient);
        } else {
            Object obj = session.getServletContext().getAttribute("udpClient");
            if (obj != null && obj instanceof ExamUDPClient) {
                udpClient = (ExamUDPClient) obj;
            }
        }
        if (map.get("Start") != null) {
            if ((login != null || !login.isEmpty()) && password != null) {
                try {
                    if (login(login, password, session.getId())) {
                        path = "/answer.html";
                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
                        requestDispatcher.forward(request, response);
                    } else {
                        pageErrorLogin(response);
                    }
                } catch (Exception ex) {
                    pageError(response, ex.toString());
                }
            }
        }
        if (map.get("Query") != null && !(user == null || user.isEmpty())) {
            try {
                int code = parseInt(user);
                if (getHistoryByCode(code, session.getId())) {
                    StringBuilder sb = new StringBuilder();
                    for (String hist : noteList) {
                        sb.append(hist).append("\n");
                    }
                    pageHistory(response, sb.toString());
                }
            } catch (Exception e) {
                try {
                    if (getHistoryByName(user, session.getId())) {
                        StringBuilder sb = new StringBuilder();
                        for (String hist : noteList) {
                            sb.append(hist).append("\n");
                        }
                        pageHistory(response, sb.toString());
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ExamWeb.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        path = "/answer.html";
        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(path);
        requestDispatcher.forward(request, response);
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
        //processRequest(request, response);
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

    private boolean login(String login, String password, String sessionId) throws SocketException, Exception {
        ExamQuery authUser = new ExamQuery(login, password, Command.LOGIN, sessionId);
        udpClient.sendMessage(authUser);
        if (udpClient != null) {
            ExamAnswer answer = udpClient.getMessage();
            if (answer != null && answer.getSessionId().equals(sessionId)) {
                return (answer.isResult());
            }
        }
        return false;

    }

    private boolean getHistoryByCode(int code, String sessionId) throws Exception {
        ExamQuery historyByCode = new ExamQuery(code, Command.GETBYCODE, sessionId);
        udpClient.sendMessage(historyByCode);
        if (udpClient != null) {
            ExamAnswer answer = udpClient.getMessage();
            if (answer != null && answer.getSessionId().equals(sessionId)) {
                noteList = answer.getArrHistory();
                return (answer.isResult());
            }
        }
        return false;
    }

    private boolean getHistoryByName(String user, String sessionId) throws ClassNotFoundException {
        ExamQuery historyByName = new ExamQuery(user, Command.GETBYNAME, sessionId);
        udpClient.sendMessage(historyByName);
        if (udpClient != null) {
            ExamAnswer answer = udpClient.getMessage();
            if (answer != null && answer.getSessionId().equals(sessionId)) {
                noteList = answer.getArrHistory();
                return (answer.isResult());
            }
        }
        return false;
    }

    private void pageErrorLogin(HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>\n"
                    + "\n"
                    + "<html>\n"
                    + "    <head>\n"
                    + "        <title>Экзаменационная работа Бабенко И.А.</title>\n"
                    + "        <meta charset=\"UTF-8\">\n"
                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "        <h1>Ошибка. Данный пользователь не найден.</h1>\n"
                    + "            <form action=\"ExamWeb\" method=\"GET\">\n"
                    + "                Введите Ваш логин:\n"
                    + "                <input type=\"text\" name=\"login\" size=\"16\" maxlength=\"16\"/>\n"
                    + "                <p>Введите Ваш пароль:\n"
                    + "                <input type=\"text\" name=\"password\" size=\"16\" maxlength=\"16\" /> \n"
                    + "                <p><input type=\"submit\" value=\"Начать сеанс\" name=\"Start\"/>\n"
                    + "            </form>\n"
                    + "    </body>\n"
                    + "</html>");
        }
    }

    private void pageError(HttpServletResponse response, String ex) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println("Ошибка " + ex);
        }
    }

    private void pageHistory(HttpServletResponse response, String hist) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html\n>"
                    + "<html>\n"
                    + "    <head>\n"
                    + "        <title>Экзаменационная работа Бабенко И.А.</title>\n"
                    + "        <meta charset=\"UTF-8\">\n"
                    + "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "        <h1>Введите запрос</h1>\n"
                    + "        <form action=\"ExamWeb\" method=\"Get\">\n"
                    + "            <p>\n"
                    + "                <label for=\"user\">Имя/код пользователя</label>\n"
                    + "                <input type=\"text\" name=\"user\" size=\"16\">\n"
                    + "                <input type=\"submit\" value=\"Отправить запрос\" name=\"Query\">\n"
                    + "<p>"
                    + "   <style>\n"
                    + "   textarea {\n"
                    + "    width: 30%; \n"
                    + "    height: 30%; \n"
                    + "   } \n"
                    + "  </style>"
                    + "<textarea name = MessageList readonly col=50 rows=30>"
                    + hist
                    + "</textarea></p>\n"
                    + "<input type=\"submit\" value=\"Выйти\" name=\"Logout\">\n"
                    + "</form>\n"
                    + "</body>\n"
                    + "</html>");
        }
    }

}
