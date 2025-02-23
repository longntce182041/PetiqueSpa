package Model;

import ConnectDB.DBConnect;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/VerifyServlet")
public class VerifyServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String verificationCode = request.getParameter("verificationCode");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            DBConnect dbConnect = new DBConnect();
            conn = dbConnect.getConnection();

            if (conn == null) {
                System.err.println("Connection to database failed!");
                response.sendRedirect("verification.jsp?error=connection_failed");
                return;
            }

            String sql = "SELECT verification_code FROM Users WHERE username = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedCode = rs.getString("verification_code");
                if (storedCode.equals(verificationCode)) {
                    // Xác thực thành công
                    response.sendRedirect("login.jsp?message=verification_success");
                } else {
                    // Xác thực thất bại
                    response.sendRedirect("verification.jsp?error=invalid_code");
                }
            } else {
                response.sendRedirect("verification.jsp?error=user_not_found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("verification.jsp?error=sql_error");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
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
}