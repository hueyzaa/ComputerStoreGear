<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ComputerStore - Trang Chủ</title>
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
            background-color: #212529 !important; /* Dark background for navbar */
        }
        .navbar-brand, .nav-link {
            color: #ffffff !important;
        }
        .navbar-brand:hover, .nav-link:hover {
            color: #cccccc !important;
        }
        .hero-section {
            background: url('https://via.placeholder.com/1500x500?text=Amazing+Deals+on+Computers') no-repeat center center/cover;
            color: white;
            text-align: center;
            padding: 100px 0;
            margin-bottom: 30px;
            position: relative;
        }
        .hero-section::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5); /* Dark overlay */
        }
        .hero-content {
            position: relative;
            z-index: 1;
        }
        .product-card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transition: transform 0.2s;
            overflow: hidden;
            margin-bottom: 20px;
        }
        .product-card:hover {
            transform: translateY(-5px);
        }
        .product-card img {
            height: 200px;
            object-fit: cover;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
        }
        .product-card .card-body {
            padding: 15px;
        }
        .product-card .card-title {
            font-size: 1.1rem;
            font-weight: bold;
            margin-bottom: 10px;
            height: 45px; /* Fixed height for title */
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2; /* Limit to 2 lines */
            -webkit-box-orient: vertical;
        }
        .product-card .card-text {
            font-size: 1.2rem;
            color: #007bff;
            font-weight: bold;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            border-color: #0056b3;
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
        .section-title {
            text-align: center;
            margin-bottom: 40px;
            font-size: 2.5rem;
            font-weight: 700;
            color: #343a40;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <!-- Hero Section -->
    <div class="hero-section">
        <div class="container hero-content">
            <h1 class="display-3 fw-bold">Nâng tầm trải nghiệm công nghệ của bạn</h1>
            <p class="lead">Khám phá những sản phẩm máy tính và linh kiện mới nhất với giá tốt nhất.</p>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg mt-3">Mua Ngay</a>
        </div>
    </div>

    <!-- Main Content - Featured Products -->
    <div class="container">
        <h2 class="section-title">Sản phẩm nổi bật</h2>
        <div class="row">
            <c:if test="${not empty products}">
                <c:forEach var="product" items="${products}" begin="0" end="7"> <%-- Display up to 8 products --%>
                    <div class="col-lg-3 col-md-4 col-sm-6">
                        <div class="card product-card">
                            <c:set var="mainImage" value="${null}"/>
                            <c:forEach var="img" items="${product.images}">
                                <c:if test="${img.mainImage}">
                                    <c:set var="mainImage" value="${img}"/>
                                </c:if>
                            </c:forEach>
                            <c:choose>
                                <c:when test="${not empty mainImage}">
                                    <img src="${pageContext.request.contextPath}/static/images/${mainImage.imageURL.substring(mainImage.imageURL.lastIndexOf('/') + 1)}" class="card-img-top" alt="${product.productName}">
                                </c:when>
                                <c:otherwise>
                                    <img src="https://via.placeholder.com/200x200?text=No+Image" class="card-img-top" alt="${product.productName}">
                                </c:otherwise>
                            </c:choose>
                            <div class="card-body">
                                <h5 class="card-title">${product.productName}</h5>
                                <p class="card-text">${product.price} VNĐ</p>
                                <a href="${pageContext.request.contextPath}/product-detail?id=${product.productID}" class="btn btn-primary btn-sm">Xem chi tiết</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
            <c:if test="${empty products}">
                <div class="col-12 text-center">
                    <p>Không có sản phẩm nào để hiển thị.</p>
                </div>
            </c:if>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>