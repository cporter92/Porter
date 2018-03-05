
package porter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

public class QuestionBean {
    
    private int chapterNo;
    private int questionNo;
    private String question; 
    private Connection connection;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String choiceE;
    private String answerKey;
    private String message;
    private String hint;
    private String answer;
    private String hostname;
    
    private void initializeDB() {
        try {
            
            // load MySQL driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Successfully loaded MySQL JBDC Driver.");
            
            // connect to the database
            connection = DriverManager.getConnection
            ("jdbc:mysql://35.185.94.191:3306/porter", "porter", "tiger");    
            System.out.println("Suceessfully connected to database.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    public QuestionBean() {
        initializeDB();
        answer = hint = answerKey = message = hostname = choiceA = choiceB = choiceC = choiceD = choiceE = "";
    }
    
    public void handleGet(HttpServletRequest request) {
        message = "";
        this.hostname = request.getRemoteAddr();
        try {
            chapterNo = Integer.parseInt(request.getParameter("chapterNo"));
            questionNo = Integer.parseInt(request.getParameter("questionNo"));
        } catch (Exception e) {
            message = "Invalid chapter/question number";
            return;
        }
       
        try {
            // TODO : CHANGE THIS PART TO USE PREPARED STATEMENT
            // temporarily using unfiltered query due to trouble using prepared statements.
            String SQL = "select * from intro11equiz where chapterNo = " + chapterNo + " and questionNo = " + questionNo;
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            //preparedStatement.setInt(1, chapterNo);
            //preparedStatement.setInt(2, questionNo);
            ResultSet rs = preparedStatement.executeQuery(SQL);
            
            while (rs.next()) {
                question = rs.getString("question");
                choiceA = rs.getString("choiceA");
                choiceB = rs.getString("choiceB");
                choiceC = rs.getString("choiceC");
                choiceD = rs.getString("choiceD");
                choiceE = rs.getString("choiceE");
                hint = rs.getString("hint");
                answerKey = rs.getString("answerKey");
            }
            question = formatQuestion();
            
            // TODO : CHANGE THIS PART TO USE PREPARED STATEMENT
            // temporarily using unfiltered query due to trouble using prepared statements.
            SQL = "select * from intro11e where chapterNo = " + chapterNo + " and questionNo = " + questionNo;
            preparedStatement = connection.prepareStatement(SQL);
            //preparedStatement.setInt(1, chapterNo);
            //preparedStatement.setInt(2, questionNo);
            rs = preparedStatement.executeQuery(SQL);
            
            while (rs.next()) {
                answer = "";
                answer += rs.getBoolean("answerA") ? "a" : "";
                answer += rs.getBoolean("answerB") ? "b" : "";
                answer += rs.getBoolean("answerC") ? "c" : "";
                answer += rs.getBoolean("answerD") ? "d" : "";
                answer += rs.getBoolean("answerE") ? "e" : "";
            }
            question = formatQuestion();
            
        } catch (Exception e) {
            message = "Invalid request parameters.";
            e.printStackTrace();
        }
        
        
    }
    
    public int getChapterNo() {
        return chapterNo;
    }
    public int getQuestionNo() {
        return questionNo;
    }
    public String getQuestion() {
        return question;
    }
    public String getChoiceA() {
        return choiceA;
    }
    public String getChoiceB() {
        return choiceB;
    }
    public String getChoiceC() {
        return choiceC;
    }
    public String getChoiceD() {
        return choiceD;
    }
    public String getChoiceE() {
        return choiceE;
    }
    public String getAnswerKey() {
        return answerKey;
    }
    public String getHint() {
        return hint;
    }
    public String getMessage() {
        return message;
    }
    public void setAnswer(String[] userChoices) {
        if (isGraded()) {
            message = "This question is already graded.";
            return;
        }
        
        String answer = "";
        for (String s: userChoices) {
            answer += s;
        }
        
        this.answer = "";
        
        if (answer.contains("a")) this.answer += "a";
      
        if (answer.contains("b")) this.answer += "b";
        
        if (answer.contains("c")) this.answer += "c";
        
        if (answer.contains("d")) this.answer += "d";
        
        if (answer.contains("e")) this.answer += "e";

        gradeAnswer();
        
    }
    
    public void gradeAnswer() {
        
        
        
        try {
            String SQL = "insert into intro11e (chapterNo, questionNo, isCorrect, hostname, "
                    + "answerA, answerB, AnswerC, AnswerD, AnswerE) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, chapterNo);
            preparedStatement.setInt(2, questionNo);

            boolean isCorrect = (answer != getAnswerKey());
            preparedStatement.setBoolean(3, isCorrect);
            preparedStatement.setString(4, this.hostname);
            for (int i = 0; i < answer.length(); i++) {
                preparedStatement.setBoolean(5, answer.contains("a"));
                preparedStatement.setBoolean(6, answer.contains("b"));
                preparedStatement.setBoolean(7, answer.contains("c"));
                preparedStatement.setBoolean(8, answer.contains("d"));
                preparedStatement.setBoolean(9, answer.contains("e"));
            }
            
            preparedStatement.executeUpdate();
            
            
        } catch (Exception e) {
          e.printStackTrace();
        }    
        
        
    }
    
    //returns true if the question has already been graded
    private boolean isGraded() {
        try {          
            // TODO : CHANGE THIS PART TO USE PREPARED STATEMENT
            // temporarily using unfiltered query due to trouble using prepared statements.
            String SQL = "select * from intro11e where chapterNo = " + chapterNo + " and questionNo = " + questionNo;
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            //preparedStatement.setInt(1, chapterNo);
            //preparedStatement.setInt(2, questionNo);
            ResultSet rs = preparedStatement.executeQuery(SQL);
            
            return rs.next();
            
        } catch (Exception e) {
            message = "Invalid request parameters.";
            e.printStackTrace();
        }
        return false;
    }    
    /* 
    split question into question/code section and colorize
    
    */
    
    private String formatQuestion() {
        String[] questionLines = question.split("\n");
        String question = questionLines[0];
        String code = "";
        for (int i = 1; i < questionLines.length; i++) {
            code += questionLines[i] + "\n";
        }
        return "<p>" + question + "</p>" + "<pre class='codeSegment'>" + colorize(code) + "</pre>";
    }
    private String colorize(String code) {
        String[] keywords = {"public", "private", "protected", "length", "for", "while"};
        String[] altKeywords = {"int", "double", "float", "boolean", "void"};
        String[] lines = code.split("\n");
        String colorizedOutput = "";
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("//")) {
                lines[i] = "<span style='color:#dddddd;'>" + lines[i] + "</span>";
            }
           
            else {
                for (String w: keywords) {
                    lines[i] = lines[i].replace(w + " ", "<span style='color:#0066ff;'>" + w + " </span>");
                }
                for (String w: altKeywords) {
                    lines[i] = lines[i].replace(w + " ", "<span style='color:#009933;'>" + w + " </span>");
                }

            }
            
            colorizedOutput += "" + lines[i] + "\n";
        }
        return colorizedOutput;
    }
    
    public String showAnswers() {
        String out = "";
        String gradeA = "";
        String gradeB = "";
        String gradeC = "";
        String gradeD = "";
        String gradeE = "";
        String gradeAChecked = "";
        String gradeBChecked = "";
        String gradeCChecked = "";
        String gradeDChecked = "";
        String gradeEChecked = "";
        
        if (isGraded()) {
       

            
            
            if ((getAnswerKey().contains("a") && !answer.contains("a")) || (!getAnswerKey().contains("a") && answer.contains("a"))) 
                gradeA = "<span style=\"color:red;font-size:22\">X</span>";
            
            if ((getAnswerKey().contains("b") && !answer.contains("b")) || (!getAnswerKey().contains("b") && answer.contains("b"))) 
                gradeB = "<span style=\"color:red;font-size:22\">X</span>";
            
            if ((getAnswerKey().contains("c") && !answer.contains("c")) || (!getAnswerKey().contains("c") && answer.contains("c"))) 
                gradeC ="<span style=\"color:red;font-size:22\">X</span>";
            
            if ((getAnswerKey().contains("d") && !answer.contains("d")) || (!getAnswerKey().contains("d") && answer.contains("d"))) 
                gradeD = "<span style=\"color:red;font-size:22\">X</span>";
            
            if ((getAnswerKey().contains("e") && !answer.contains("e")) || (!getAnswerKey().contains("e") && answer.contains("e"))) 
                gradeE = "<span style=\"color:red;font-size:22\">X</span>";
            
            if (answer.contains("a")) {
                gradeAChecked = "checked";
            }
            if (answer.contains("b")) {
                gradeBChecked = "checked";
            }
            if (answer.contains("c")) {
                gradeCChecked = "checked";
            }
            if (answer.contains("d")) {
                gradeDChecked = "checked";
            }
            if (answer.contains("e")) {
                gradeEChecked = "checked";
            }
            
            if (getAnswerKey().contains("a")) {
                gradeA = "<span style=\"color:green;font-size:22\">✔</span>";
            }
            if (getAnswerKey().contains("b")) {
                gradeB = "<span style=\"color:green;font-size:22\">✔</span>";
            }
            if (getAnswerKey().contains("c")) {
                gradeC = "<span style=\"color:green;font-size:22\">✔</span>";
            }
            if (getAnswerKey().contains("d")) {
                gradeD = "<span style=\"color:green;font-size:22\">✔</span>";
            }
            if (getAnswerKey().contains("e")) {
                gradeE = "<span style=\"color:green;font-size:22\">✔</span>";
            }
            
            
        }
        
        
        if (getAnswerKey().trim().length() == 1) {
            if (!getChoiceA().isEmpty())
                out += "<span class=\"choice\"><input type=\"radio\" value=\"a\" name=\"answer\" "+gradeAChecked+" ><span class=\"key\">A.</span>" + getChoiceA() + gradeA + "</span>";
            if (!getChoiceB().isEmpty())
                out += "<span class=\"choice\"><input type=\"radio\" value=\"b\" name=\"answer\" "+gradeBChecked+" ><span class=\"key\" >B.</span>" + getChoiceB() + gradeB +"</span>";
            if (!getChoiceC().isEmpty())
                out += "<span class=\"choice\"><input type=\"radio\" value=\"c\" name=\"answer\" "+gradeCChecked+"><span class=\"key\">C.</span>" + getChoiceC() + gradeC + "</span>";
            if (!getChoiceD().isEmpty())
                out += "<span class=\"choice\"><input type=\"radio\" value=\"d\" name=\"answer\" "+gradeDChecked+"><span class=\"key\">D.</span>" + getChoiceD() + gradeD + "</span>";
            if (!getChoiceE().isEmpty())
                out += "<span class=\"choice\"><input type=\"radio\" value=\"e\" name=\"answer\" "+gradeEChecked+"><span class=\"key\">E.</span>" + getChoiceE() + gradeE + "</span>";   
        } else {
            if (!getChoiceA().isEmpty())
                out += "<span class=\"choice\"><input type=\"checkbox\" value=\"a\" name=\"answer\" "+gradeAChecked+"><span class=\"key\">A.</span>" + getChoiceA() + gradeA +"</span>";
            if (!getChoiceB().isEmpty())
                out += "<span class=\"choice\"><input type=\"checkbox\" value=\"b\" name=\"answer\" "+gradeBChecked+"><span class=\"key\">B.</span>" + getChoiceB() + gradeB + "</span>";
            if (!getChoiceC().isEmpty())
                out += "<span class=\"choice\"><input type=\"checkbox\" value=\"c\" name=\"answer\" "+gradeCChecked+"><span class=\"key\">C.</span>" + getChoiceC() + gradeC + "</span>";
            if (!getChoiceD().isEmpty())
                out += "<span class=\"choice\"><input type=\"checkbox\" value=\"d\" name=\"answer\" "+gradeDChecked+"><span class=\"key\">D.</span>" + getChoiceD() + gradeD + "</span>";
            if (!getChoiceE().isEmpty())
                out += "<span class=\"choice\"><input type=\"checkbox\" value=\"e\" name=\"answer\" "+gradeEChecked+"><span class=\"key\">E.</span>" + getChoiceE() + gradeE + "</span>"; 
        }
         if (isGraded()) out += "\n" + getHint();
        return out;
    }
}
