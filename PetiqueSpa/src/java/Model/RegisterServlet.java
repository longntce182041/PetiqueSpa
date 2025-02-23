package Model;

import ConnectDB.DBConnect;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            DBConnect dbConnect = new DBConnect();
            conn = dbConnect.getConnection();

            if (conn == null) {
                System.err.println("Connection to database failed!");
                response.sendRedirect("register.jsp?error=connection_failed");
                return;
            }

            String sql = "INSERT INTO Users (username, password, email, phone) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password); // Lưu ý: Nên mã hóa mật khẩu trước khi lưu
            pstmt.setString(3, email);
            pstmt.setString(4, phone);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                // Gửi email xác thực
                String verificationCode = generateVerificationCode();
                sendVerificationEmail(email, verificationCode);

                // Lưu mã xác thực vào cơ sở dữ liệu (bạn cần tự triển khai phần này)
                saveVerificationCode(username, verificationCode);

                response.sendRedirect("verification.jsp");
            } else {
                response.sendRedirect("register.jsp?error=registration_failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=sql_error");
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String generateVerificationCode() {
        // Tạo mã xác thực ngẫu nhiên (bạn có thể tùy chỉnh cách tạo mã này)
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    private void sendVerificationEmail(String email, String verificationCode) {
        final String fromEmail = "your-email@example.com"; // Địa chỉ email của bạn
        final String password = "your-email-password"; // Mật khẩu email của bạn

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.example.com"); // SMTP Host
        props.put("mail.smtp.port", "587"); // TLS Port
        props.put("mail.smtp.auth", "true"); // Enable authentication
        props.put("mail.smtp.starttls.enable", "true"); // Enable STARTTLS

        // Tạo session
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Email Verification");
            message.setText("Please verify your email using this code: " + verificationCode);

            // Gửi email
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void saveVerificationCode(String username, String verificationCode) {
        // Lưu mã xác thực vào cơ sở dữ liệu (bạn cần tự triển khai phần này)
    }
}