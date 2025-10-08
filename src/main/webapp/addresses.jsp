<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Địa chỉ của bạn</title>
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
        .address-card {
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            background-color: #fff;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            position: relative;
        }
        .address-card h3 {
            color: #007bff;
            margin-bottom: 15px;
        }
        .address-card .default-badge {
            background-color: #28a745;
            color: white;
            padding: 5px 10px;
            border-radius: 5px;
            font-size: 0.8em;
            position: absolute;
            top: 15px;
            right: 15px;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        <h1 class="text-center mb-4">Quản lý Địa chỉ của bạn</h1>

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

        <c:if test="${empty requestScope.addresses}">
            <div class="alert alert-info text-center" role="alert">
                Bạn chưa có địa chỉ nào được lưu.
            </div>
        </c:if>

        <div class="row row-cols-1 row-cols-md-2 g-4">
            <c:forEach var="address" items="${requestScope.addresses}">
                <div class="col">
                    <div class="address-card">
                        <c:if test="${address.isDefault}">
                            <span class="default-badge">Mặc định</span>
                        </c:if>
                        <h3 class="mb-3">${address.addressType}</h3>
                        <p class="mb-1">${address.addressLine1}</p>
                        <c:if test="${not empty address.addressLine2}">
                            <p class="mb-1">${address.addressLine2}</p>
                        </c:if>
                        <p class="mb-1">${address.city}, ${address.state} ${address.postalCode}</p>
                        <p class="mb-3">${address.country}</p>
                        <div class="address-actions d-flex flex-wrap gap-2">
                            <a href="${pageContext.request.contextPath}/profile/addresses/edit?id=${address.addressID}" class="btn btn-sm btn-warning"><i class="fas fa-edit"></i> Sửa</a>
                            <a href="${pageContext.request.contextPath}/profile/addresses/delete?id=${address.addressID}" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa địa chỉ này không?');"><i class="fas fa-trash"></i> Xóa</a>
                            <c:if test="${!address.isDefault}">
                                <a href="${pageContext.request.contextPath}/profile/addresses/set-default?id=${address.addressID}" class="btn btn-sm btn-info text-white"><i class="fas fa-star"></i> Đặt làm mặc định</a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/profile/addresses/add" class="btn btn-success"><i class="fas fa-plus-circle"></i> Thêm địa chỉ mới</a>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>