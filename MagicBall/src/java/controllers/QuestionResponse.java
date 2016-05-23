/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import extras.Extras;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import model.Answer;

/**
 *
 * @author ivanmagda
 */
public class QuestionResponse extends HttpServlet {

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private UserTransaction userTransaction;

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
        try (PrintWriter out = response.getWriter()) {
            String question = request.getParameter(Extras.QUESTION_PARAMETER_KEY);
            Answer answer = getRandomAnswer();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Question Answer</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Вы задали вопрос: " + question + "</h1>");
            out.println("<h1>Мой ответ: " + answer.getTitle() + ".</h1>");
            out.println("<button type=\"button\" name=\"back\" onclick=\"history.back()\">Назад</button>");
            out.println("</body>");
            out.println("</html>");
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
    }

    private Answer getRandomAnswer() {
        List<Answer> answers = getAllAnswers();
        
        Random random = new Random();
        int index = random.nextInt(answers.size());
        
        return answers.get(index);
    }
    
    private List<Answer> getAllAnswers() {
        return entityManager.createQuery("SELECT x FROM Answer x").getResultList();
    }

    private void persistAnswers() {
        String[] answersArr = {
            "Бесспорно", "Предрешено", "Никаких сомнений", "Определѐнно да",
            "Можешь быть уверен в этом", "Мне кажется – «да»", "Вероятнее всего",
            "Хорошие перспективы", "Знаки говорят – «да»", "Да", "Пока не ясно, попробуй снова",
            "Спроси позже", "Лучше не рассказывать", "Сейчас нельзя предсказать",
            "Сконцентрируйся и спроси опять", "Даже не думай", "Мой ответ – «нет»",
            "По моим данным – «нет»", "Перспективы не очень хорошие", "Весьма сомнительно"
        };

        try {
            userTransaction.begin();

            for (String anAnswer : answersArr) {
                Answer answer = new Answer();
                answer.setTitle(anAnswer);
                entityManager.persist(answer);
            }

            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(QuestionResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
