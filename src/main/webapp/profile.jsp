<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ của tôi</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
        }
        .navbar {
            background-color: #212529 !important;
        }
        .navbar-brand, .nav-link {
            color: #ffffff !important;
        }
        .navbar-brand:hover, .nav-link:hover {
            color: #cccccc !important;
        }
        .footer {
            background-color: #212529;
            color: white;
            padding: 30px 0;
            text-align: center;
            margin-top: 50px;
        }
        .footer a {
            color: #cccccc;
            text-decoration: none;
        }
        .footer a:hover {
            color: #ffffff;
        }
        .profile-info-card, .profile-form-card {
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
        .profile-info-card h2, .profile-form-card h2 {
            color: #343a40;
            margin-bottom: 20px;
            font-weight: 700;
            text-align: center;
        }
        .profile-info-card p {
            margin-bottom: 10px;
        }
        .profile-info-card .label {
            font-weight: bold;
            color: #555;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        <h1 class="text-center mb-4">Hồ sơ của tôi</h1>

        <c:if test="${not empty param.message}">
            <div class="alert alert-success text-center" role="alert">
                ${param.message}
            </div>
        </c:if>
        <c:if test="${not empty requestScope.errorMessage}">
            <div class="alert alert-danger text-center" role="alert">
                ${requestScope.errorMessage}
            </div>
        </c:if>

        <div class="profile-info-card">
            <h2>Thông tin cá nhân</h2>
            <p><span class="label">Tên đăng nhập:</span> ${requestScope.userProfile.username}</p>
            <p><span class="label">Email:</span> ${requestScope.userProfile.email}</p>
            <p><span class="label">Họ và tên:</span> ${requestScope.userProfile.firstName} ${requestScope.userProfile.lastName}</p>
            <p><span class="label">Số điện thoại:</span> ${requestScope.userProfile.phoneNumber}</p>
            <p><span class="label">Ngày sinh:</span> <fmt:formatDate value="${requestScope.userProfileDateOfBirthUtil}" pattern="dd/MM/yyyy"/></p>
            <p><span class="label">Giới tính:</span> ${requestScope.userProfile.gender}</p>
            <p><span class="label">Vai trò:</span> ${requestScope.userProfile.role}</p>
            <p><span class="label">Email đã xác minh:</span> <c:choose><c:when test="${requestScope.userProfile.emailVerified}">Có</c:when><c:otherwise>Không</c:otherwise></c:choose></p>
            <p><span class="label">Trạng thái tài khoản:</span> <c:choose><c:when test="${requestScope.userProfile.active}">Hoạt động</c:when><c:otherwise>Không hoạt động</c:otherwise></c:choose></p>
        </div>

        <div class="profile-form-card">
            <h2>Cập nhật Hồ sơ</h2>
            <form action="${pageContext.request.contextPath}/profile/edit" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">Tên đăng nhập:</label>
                    <input type="text" class="form-control" id="username" name="username" value="${requestScope.userProfile.username}" required>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email:</label>
                    <input type="email" class="form-control" id="email" name="email" value="${requestScope.userProfile.email}" required>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="firstName" class="form-label">Tên:</label>
                        <input type="text" class="form-control" id="firstName" name="firstName" value="${requestScope.userProfile.firstName}" required>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label for="lastName" class="form-label">Họ:</label>
                        <input type="text" class="form-control" id="lastName" name="lastName" value="${requestScope.userProfile.lastName}" required>
                    </div>
                </div>
                <div class="mb-3">
                    <label for="phoneNumber" class="form-label">Số điện thoại:</label>
                    <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" value="${requestScope.userProfile.phoneNumber}">
                </div>
                <div class="mb-3">
                    <label for="dateOfBirth" class="form-label">Ngày sinh:</label>
                    <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" value="<fmt:formatDate value='${requestScope.userProfileDateOfBirthUtil}' pattern='yyyy-MM-dd'/>">
                </div>
                <div class="mb-3">
                    <label for="gender" class="form-label">Giới tính:</label>
                    <select class="form-select" id="gender" name="gender">
                        <option value="">Chọn giới tính</option>
                        <option value="Male" <c:if test="${requestScope.userProfile.gender == 'Male'}">selected</c:if>>Nam</option>
                        <option value="Female" <c:if test="${requestScope.userProfile.gender == 'Female'}">selected</c:if>>Nữ</option>
                        <option value="Other" <c:if test="${requestScope.userProfile.gender == 'Other'}">selected</c:if>>Khác</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary w-100">Cập nhật Hồ sơ</button>
            </form>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>