package dao;

import model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/student_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Huyhoang12a1";
    
    // Get database connection
    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }
    
    // Get student by ID
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new student
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (student_code, full_name, email, major) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update student
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET student_code = ?, full_name = ?, email = ?, major = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, student.getStudentCode());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMajor());
            pstmt.setInt(5, student.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete student
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Search students
    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();

        // If keyword is empty → return all
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }

        String sql = "SELECT * FROM students "
                   + "WHERE student_code LIKE ? OR full_name LIKE ? OR email LIKE ? "
                   + "ORDER BY id DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";

            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    // ==========================================
    // NEW METHODS ADDED BELOW
    // ==========================================

    // Helper: Validate Sort Column to prevent SQL Injection
    private String validateSortBy(String sortBy) {
        // List of allowed columns
        String[] validColumns = {"id", "student_code", "full_name", "email", "major"};
        
        if (sortBy == null) return "id";
        
        for (String col : validColumns) {
            if (col.equalsIgnoreCase(sortBy)) {
                return col;
            }
        }
        return "id"; // Default fallback
    }

    // Helper: Validate Order direction
    private String validateOrder(String order) {
        if ("desc".equalsIgnoreCase(order)) {
            return "DESC";
        }
        return "ASC"; // Default fallback
    }
    
    // Helper: Map ResultSet to Student object (Reduces code duplication)
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentCode(rs.getString("student_code"));
        student.setFullName(rs.getString("full_name"));
        student.setEmail(rs.getString("email"));
        student.setMajor(rs.getString("major"));
        student.setCreatedAt(rs.getTimestamp("created_at"));
        return student;
    }

    // Method 1: Sort Students
    public List<Student> getStudentsSorted(String sortBy, String order) {
        List<Student> students = new ArrayList<>();
        
        // Validate inputs
        String validSort = validateSortBy(sortBy);
        String validOrder = validateOrder(order);
        
        // Build SQL string safe from injection due to validation above
        String sql = "SELECT * FROM students ORDER BY " + validSort + " " + validOrder;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }

    // Method 2: Filter Students by Major
    public List<Student> getStudentsByMajor(String major) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE major = ? ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, major);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }

    // Challenge: Combined Filter and Sort
    public List<Student> getStudentsFiltered(String major, String sortBy, String order) {
        List<Student> students = new ArrayList<>();
        
        // Validate sort params
        String validSort = validateSortBy(sortBy);
        String validOrder = validateOrder(order);
        
        // Start SQL builder
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM students WHERE 1=1");
        
        // Check if major filter is needed
        boolean hasMajor = (major != null && !major.trim().isEmpty() && !"all".equalsIgnoreCase(major));
        
        if (hasMajor) {
            sqlBuilder.append(" AND major = ?");
        }
        
        // Append sorting
        sqlBuilder.append(" ORDER BY ").append(validSort).append(" ").append(validOrder);
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
            
            // Set parameter if major filter is active
            if (hasMajor) {
                pstmt.setString(1, major);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return students;
    }

    // File: dao/StudentDAO.java

// Thay thế đoạn code cũ: throw new UnsupportedOperationException...
// Bằng đoạn code mới này:
public int getTotalStudents() {
    int total = 0;
    String sql = "SELECT COUNT(*) FROM students";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        if (rs.next()) {
            total = rs.getInt(1);
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return total;
}
}