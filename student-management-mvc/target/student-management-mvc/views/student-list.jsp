<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student List - MVC</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
            font-size: 32px;
        }
        
        .subtitle {
            color: #666;
            margin-bottom: 30px;
            font-style: italic;
        }
        
        .message {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
            font-weight: 500;
        }
        
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .btn {
            display: inline-block;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-danger {
            background-color: #dc3545;
            color: white;
            padding: 8px 16px;
            font-size: 13px;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        thead {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }
        
        /* Sort Link Styles */
        th a {
            color: white;
            text-decoration: none;
            display: flex;
            align-items: center;
            gap: 5px;
        }
        
        th a:hover {
            text-decoration: underline;
            color: #f0f0f0;
        }
        
        tbody tr {
            transition: background-color 0.2s;
        }
        
        tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .actions {
            display: flex;
            gap: 10px;
        }
        
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }
        
        .empty-state-icon {
            font-size: 64px;
            margin-bottom: 20px;
        }

        /* Search & Filter Section Styles */
        .controls-container {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #e9ecef;
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            justify-content: space-between;
            align-items: flex-start;
        }

        .search-box, .filter-box {
            flex: 1;
            min-width: 300px;
        }

        .control-form {
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .input-field, .select-field {
            flex-grow: 1;
            padding: 10px;
            border: 1px solid #ced4da;
            border-radius: 5px;
            font-size: 14px;
        }
        
        .select-field {
            cursor: pointer;
        }

        .input-field:focus, .select-field:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        
        .label-title {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #555;
            font-size: 0.9rem;
        }

        .result-msg {
            margin-top: 10px;
            color: #555;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📚 Student Management System</h1>
        <p class="subtitle">MVC Pattern with Jakarta EE & JSTL</p>
        
        <c:if test="${not empty param.message}">
            <div class="message success">✅ ${param.message}</div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="message error">❌ ${param.error}</div>
        </c:if>
        
        <div class="controls-container">
            
            <div class="search-box">
                <span class="label-title">Search Students</span>
                <form action="student" method="get" class="control-form">
                    <input type="hidden" name="action" value="search">
                    <input type="text" name="keyword" class="input-field" 
                           placeholder="Name, Email, or ID..." value="${keyword}">
                    <button type="submit" class="btn btn-primary">🔍</button>
                    <c:if test="${not empty keyword}">
                        <a href="student?action=list" class="btn btn-secondary">❌</a>
                    </c:if>
                </form>
                <c:if test="${not empty keyword}">
                    <div class="result-msg">Results for: <strong>"${keyword}"</strong></div>
                </c:if>
            </div>

            <div class="filter-box">
                <span class="label-title">Filter by Major</span>
                <form action="student" method="get" class="control-form">
                    <input type="hidden" name="action" value="filter">
                    <select name="major" class="select-field" onchange="this.form.submit()">
                        <option value="">All Majors</option>
                        <option value="Computer Science" ${major == 'Computer Science' ? 'selected' : ''}>
                            Computer Science
                        </option>
                        <option value="Information Technology" ${major == 'Information Technology' ? 'selected' : ''}>
                            Information Technology
                        </option>
                        <option value="Software Engineering" ${major == 'Software Engineering' ? 'selected' : ''}>
                            Software Engineering
                        </option>
                        <option value="Business Administration" ${major == 'Business Administration' ? 'selected' : ''}>
                            Business Administration
                        </option>
                    </select>
                    <button type="submit" class="btn btn-primary">Filter</button>
                    <c:if test="${not empty major && major != 'all'}">
                        <a href="student?action=list" class="btn btn-secondary">❌</a>
                    </c:if>
                </form>
            </div>
        </div>
        
        <div style="margin-bottom: 20px;">
            <a href="student?action=new" class="btn btn-primary">➕ Add New Student</a>
        </div>
        
        <c:choose>
            <c:when test="${not empty students}">
                <table>
                    <thead>
                        <tr>
                            <th>
                                <a href="student?action=sort&sortBy=id&order=${sortBy == 'id' && order == 'asc' ? 'desc' : 'asc'}">
                                    ID <c:if test="${sortBy == 'id'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=student_code&order=${sortBy == 'student_code' && order == 'asc' ? 'desc' : 'asc'}">
                                    Student Code <c:if test="${sortBy == 'student_code'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=full_name&order=${sortBy == 'full_name' && order == 'asc' ? 'desc' : 'asc'}">
                                    Full Name <c:if test="${sortBy == 'full_name'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=email&order=${sortBy == 'email' && order == 'asc' ? 'desc' : 'asc'}">
                                    Email <c:if test="${sortBy == 'email'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                                </a>
                            </th>
                            
                            <th>
                                <a href="student?action=sort&sortBy=major&order=${sortBy == 'major' && order == 'asc' ? 'desc' : 'asc'}">
                                    Major <c:if test="${sortBy == 'major'}">${order == 'asc' ? '▲' : '▼'}</c:if>
                                </a>
                            </th>
                            
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.id}</td>
                                <td><strong>${student.studentCode}</strong></td>
                                <td>${student.fullName}</td>
                                <td>${student.email}</td>
                                <td>${student.major}</td>
                                <td>
                                    <div class="actions">
                                        <a href="student?action=edit&id=${student.id}" class="btn btn-secondary">✏️ Edit</a>
                                        <a href="student?action=delete&id=${student.id}" 
                                           class="btn btn-danger"
                                           onclick="return confirm('Are you sure you want to delete this student?')">
                                            🗑️ Delete
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📭</div>
                    <h3>No students found</h3>
                    <p>
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                Try different keywords or clear the search.
                            </c:when>
                            <c:when test="${not empty major}">
                                No students found in <strong>${major}</strong>. Clear the filter to see all.
                            </c:when>
                            <c:otherwise>
                                Start by adding a new student
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>