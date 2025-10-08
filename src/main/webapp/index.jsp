<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chào mừng đến với ComputerStore</title>
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
        .main-content {
            min-height: calc(100vh - 150px); /* Adjust based on header/footer height */
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 50px 0;
        }
        .welcome-card {
            background-color: #fff;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
            max-width: 600px;
            width: 100%;
        }
        .welcome-card h1 {
            color: #007bff;
            margin-bottom: 20px;
            font-weight: 700;
        }
        .welcome-card p {
            color: #555;
            line-height: 1.6;
            margin-bottom: 30px;
        }
        .welcome-card .btn {
            margin: 0 10px;
            padding: 12px 25px;
            font-size: 1.1rem;
            border-radius: 5px;
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
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="main-content">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-8">
                    <div class="welcome-card">
                        <h1>Chào mừng đến với ComputerStore!</h1>
                        <p>Đây là trang chủ của ứng dụng. Vui lòng chọn một tùy chọn bên dưới để tiếp tục.</p>
                        <div class="links">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập</a>
                            <a href="${pageContext.request.contextPath}/register" class="btn btn-success">Đăng ký</a>
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-info text-white">Xem Sản phẩm</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>