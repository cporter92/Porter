<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id = "questionBean" class = "porter.QuestionBean" scope="session" /> 
 <jsp:setProperty name = "questionBean" property = "*"  />
<!DOCTYPE html>
<% questionBean.handleGet(request); %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Multiple-Choice Question</title>
        <style>
            .body {
                color: black;
            }
            
            .titleWrapper {
                width:100%;
                background-color:#6193cb;
                color: white;
                margin: 0;
                text-align:center;
            }
            .questionWrapper {
                border: 1px solid #f6912f;
                padding: 20px;
                padding-top:5px;
                margin: 0;
                color: black;
            }
            .wrapper {
                width: 600px;
                margin: 10px auto;
            }
            .codeSegment {
                font-family: monospace;
                font-size: 13px;
            }
            .submitButton {
                background-color:#009933;
                border:none;
                display:block;
                margin: 10px 0px;
            }
            .answerWrapper {
                font-size:14px;
            }
            .key {
                margin-right:20px;
                margin-left: 5px;
                margin-top: 5px;
                margin-bottom: 5px;
            }
            .choice {
                display:block;
            }
            
            
        </style>
    </head>
    <% if (request.getParameter("answer") != null) {
        questionBean.setAnswer(request.getParameterValues("answer"));
    }
    %>
    
    <body>
        <div class="wrapper">
            <jsp:getProperty name = "questionBean" property = "message"/>
            <div class="titleWrapper">
                Multiple Choice Question Number <jsp:getProperty name = "questionBean" property = "chapterNo"/>.<jsp:getProperty name = "questionBean" property = "questionNo"/>
            </div>
            <div class="questionWrapper">
                <p><jsp:getProperty name = "questionBean" property = "question"/></p>
                <form class="answerWrapper" action="OneQuestion.jsp" method="get">
                    
                    <%= questionBean.showAnswers() %>
                    
                    <button class="submitButton">Check My Answer</button>
                    <input type="hidden" name="questionNo" value="<jsp:getProperty name="questionBean" property="questionNo"/>"/>
                    <input type="hidden" name="chapterNo" value="<jsp:getProperty name="questionBean" property="chapterNo"/>"/>
                </form>
            </div>
                   
        </div>
    </body>
</html>
