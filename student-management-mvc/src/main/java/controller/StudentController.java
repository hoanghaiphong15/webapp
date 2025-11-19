package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
                searchStudents(request, response);
                break;
            // [ADDED] Cases for Sort and Filter
            case "sort":
                sortStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    // --- LIST, SEARCH, SORT & FILTER METHODS ---
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Student> students = studentDAO.getAllStudents();
        request.setAttribute("students", students);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        List<Student> students;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            students = studentDAO.searchStudents(keyword);
        } else {
            students = studentDAO.getAllStudents();
        }
        
        request.setAttribute("students", students);
        request.setAttribute("keyword", keyword);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // [NEW] Sort Students Method
    private void sortStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get sort parameters
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");
        
        // 2. Call DAO method
        List<Student> students = studentDAO.getStudentsSorted(sortBy, order);
        
        // 3. Set attributes (results + current sort state)
        request.setAttribute("students", students);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);
        
        // 4. Forward to view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }

    // [NEW] Filter Students Method
    private void filterStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get filter parameter
        String major = request.getParameter("major");
        
        List<Student> students;
        
        // Handle "All Majors" case or empty selection
        if (major == null || major.trim().isEmpty() || major.equalsIgnoreCase("all")) {
            students = studentDAO.getAllStudents();
        } else {
            // 2. Call DAO method for specific major
            students = studentDAO.getStudentsByMajor(major);
        }
        
        // 3. Set attributes (results + current filter state)
        request.setAttribute("students", students);
        request.setAttribute("major", major);
        
        // 4. Forward to view
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    // --- FORM DISPLAY METHODS ---

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // --- CREATE, UPDATE, DELETE METHODS ---

    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student newStudent = new Student(studentCode, fullName, email, major);
        
        // VALIDATION CHECK
        if (!validateStudent(newStudent, request)) {
            request.setAttribute("student", newStudent);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        if (studentDAO.addStudent(newStudent)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to add student");
        }
    }
    
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, ServletException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        student.setId(id);
        
        // VALIDATION CHECK
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to update student");
        }
    }
    
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }

    // --- VALIDATION HELPER METHOD ---
    
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;
        
        // 1. Validate Student Code
        String code = student.getStudentCode();
        String codePattern = "[A-Z]{2}[0-9]{3,}";
        
        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
            isValid = false;
        } else if (!code.matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 UPPERCASE letters + 3 digits (e.g., SV001)");
            isValid = false;
        }
        
        // 2. Validate Full Name
        String name = student.getFullName();
        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (name.trim().length() < 2) {
            request.setAttribute("errorName", "Name must be at least 2 characters");
            isValid = false;
        }
        
        // 3. Validate Email
        String email = student.getEmail();
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        
        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format");
                isValid = false;
            }
        }
        
        // 4. Validate Major
        String major = student.getMajor();
        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required");
            isValid = false;
        }
        
        return isValid;
    }
}