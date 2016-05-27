/*
 * The MIT License
 *
 * Copyright 2016 ivanmagda.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import model.Author;
import model.Quote;

/**
 *
 * @author ivanmagda
 */
public class QuoteServlet extends HttpServlet {

    @Resource
    UserTransaction userTransaction;

    @PersistenceContext
    EntityManager entityManager;

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
            persistDataIfNeeded();
            Quote quote = getRandomQuote();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Quote</title>");
            out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
            out.println("<link rel=\"stylesheet\" href=\"style.css\">");
            out.println("<link rel=\"stylesheet\" href=\"bootstrap/css/bootstrap.min.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"container\">");
            out.println("<div class=\"quote\">");
            out.println("<h1>\"" + quote.getTitle() + "\"</h1>");
            out.println("<h3>" + quote.getAuthor().getName() + "</h3>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private Quote getRandomQuote() {
        Query countQuery = entityManager.createNativeQuery("SELECT count(*) FROM Quote");
        int count = (int) countQuery.getSingleResult();

        Random random = new Random();
        int idx = random.nextInt(count);

        Query selectQuery = entityManager.createQuery("SELECT q FROM Quote q");
        selectQuery.setFirstResult(idx);
        selectQuery.setMaxResults(1);

        return (Quote) selectQuery.getSingleResult();
    }

    private void persistDataIfNeeded() {
        try {
            userTransaction.begin();

            Query countQuery = entityManager.createNativeQuery("SELECT count(*) FROM Quote");
            int count = (int) countQuery.getSingleResult();
            if (count == 0) {
                Author abrahamLincoln = new Author();
                abrahamLincoln.setName("Авраам Линкольн");
                abrahamLincoln.setQuotes(new ArrayList<>());
                entityManager.persist(abrahamLincoln);

                String[] lincolnQuotes = {
                    "Когда я делаю добро, я чувствую себя хорошо. Когда я поступаю плохо, я чувствую себя плохо. Вот моя религия.",
                    "Мы не поможем людям, делая за них то, что они могли бы сделать сами."
                };
                persistQuotesFromArray(lincolnQuotes, abrahamLincoln);

                Author albertEinstein = new Author();
                albertEinstein.setName("Альберт Эйнштейн");
                albertEinstein.setQuotes(new ArrayList<>());
                entityManager.persist(albertEinstein);

                String[] einsteinQuotes = {
                    "Стремись не к тому, чтобы добиться успеха, а к тому, чтобы твоя жизнь имела смысл.",
                    "Достойна только та жизнь, которая прожита ради других людей."
                };
                persistQuotesFromArray(einsteinQuotes, albertEinstein);
            }

            userTransaction.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(QuoteServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void persistQuotesFromArray(String[] quotes, Author author) {
        if (quotes == null || quotes.length == 0) {
            return;
        }

        for (String quoteString : quotes) {
            Quote quote = new Quote();
            quote.setTitle(quoteString);
            quote.setAuthor(author);
            entityManager.persist(quote);

            if (author.getQuotes() == null) {
                author.setQuotes(new ArrayList<>());
            }
            author.getQuotes().add(quote);
            entityManager.merge(author);
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
