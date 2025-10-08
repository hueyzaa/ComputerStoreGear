<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:choose><c:when test="${not empty requestScope.address}">Sửa Địa chỉ</c:when><c:otherwise>Thêm Địa chỉ mới</c:otherwise></c:choose></title>
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
        .address-form-card {
            background-color: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        .address-form-card h1 {
            color: #343a40;
            margin-bottom: 30px;
            font-weight: 700;
            text-align: center;
        }
        .address-form-card .form-label {
            font-weight: 500;
        }
        .address-form-card .btn-primary {
            width: 100%;
            padding: 10px;
            font-size: 1.1rem;
            margin-top: 15px;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                <div class="address-form-card">
                    <h1><c:choose><c:when test="${not empty requestScope.address}">Sửa Địa chỉ</c:when><c:otherwise>Thêm Địa chỉ mới</c:otherwise></c:choose></h1>
                    <c:if test="${not empty requestScope.errorMessage}">
                        <div class="alert alert-danger" role="alert">
                            ${requestScope.errorMessage}
                        </div>
                    </c:if>
                    <c:if test="${not empty requestScope.successMessage}">
                        <div class="alert alert-success" role="alert">
                            ${requestScope.successMessage}
                        </div>
                    </c:if>
                    <form action="${pageContext.request.contextPath}/profile/addresses/<c:choose><c:when test="${not empty requestScope.address}">edit</c:when><c:otherwise>add</c:otherwise></c:choose>" method="post">
                        <c:if test="${not empty requestScope.address}">
                            <input type="hidden" name="addressId" value="${requestScope.address.addressID}">
                        </c:if>
                        <div class="mb-3">
                            <label for="addressType" class="form-label">Loại địa chỉ:</label>
                            <select class="form-select" id="addressType" name="addressType" required>
                                <option value="">Chọn loại địa chỉ</option>
                                <option value="Shipping" <c:if test="${requestScope.address.addressType == 'Shipping'}">selected</c:if>>Giao hàng</option>
                                <option value="Billing" <c:if test="${requestScope.address.addressType == 'Billing'}">selected</c:if>>Thanh toán</option>
                                <option value="Home" <c:if test="${requestScope.address.addressType == 'Home'}">selected</c:if>>Nhà riêng</option>
                                <option value="Work" <c:if test="${requestScope.address.addressType == 'Work'}">selected</c:if>>Cơ quan</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="addressLine1" class="form-label">Địa chỉ dòng 1:</label>
                            <input type="text" class="form-control" id="addressLine1" name="addressLine1" value="${requestScope.address.addressLine1}" required>
                        </div>
                        <div class="mb-3">
                            <label for="addressLine2" class="form-label">Địa chỉ dòng 2 (Tùy chọn):</label>
                            <input type="text" class="form-control" id="addressLine2" name="addressLine2" value="${requestScope.address.addressLine2}">
                        </div>
                        <div class="mb-3">
                            <label for="city" class="form-label">Thành phố:</label>
                            <input type="text" class="form-control" id="city" name="city" value="${requestScope.address.city}" required>
                        </div>
                        <div class="mb-3">
                            <label for="state" class="form-label">Tỉnh/Bang (Tùy chọn):</label>
                            <input type="text" class="form-control" id="state" name="state" value="${requestScope.address.state}">
                        </div>
                        <div class="mb-3">
                            <label for="postalCode" class="form-label">Mã bưu chính (Tùy chọn):</label>
                            <input type="text" class="form-control" id="postalCode" name="postalCode" value="${requestScope.address.postalCode}">
                        </div>
                        <div class="mb-3">
                            <label for="country" class="form-label">Quốc gia:</label>
                            <input type="text" class="form-control" id="country" name="country" value="${requestScope.address.country}" required>
                        </div>
                        <div class="form-check mb-3">
                            <input class="form-check-input" type="checkbox" id="isDefault" name="isDefault" <c:if test="${requestScope.address.isDefault}">checked</c:if>>
                            <label class="form-check-label" for="isDefault">Đặt làm địa chỉ mặc định</label>
                        </div>
                        <button type="submit" class="btn btn-primary"><c:choose><c:when test="${not empty requestScope.address}">Cập nhật Địa chỉ</c:when><c:otherwise>Thêm Địa chỉ</c:otherwise></c:choose></button>
                    </form>
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/profile/addresses" class="btn btn-secondary">Quay lại danh sách địa chỉ</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>