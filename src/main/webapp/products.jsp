<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh Sách Sản Phẩm</title>
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
        .product-card {
            border: none;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transition: transform 0.2s;
            overflow: hidden;
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
            color: #dc3545;
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

    <div class="container mt-4">
        <h1 class="text-center mb-4">Danh Sách Sản Phẩm</h1>

        <div class="search-filter-bar p-3 mb-4 rounded shadow-sm">
            <form action="${pageContext.request.contextPath}/products" method="get" class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label for="searchTerm" class="form-label visually-hidden">Tìm kiếm sản phẩm...</label>
                    <input type="text" class="form-control" id="searchTerm" name="searchTerm" placeholder="Tìm kiếm sản phẩm..." value="${selectedSearchTerm}">
                </div>
                <div class="col-md-2">
                    <label for="categoryId" class="form-label visually-hidden">Danh mục</label>
                    <select class="form-select" id="categoryId" name="categoryId">
                        <option value="">Tất cả Danh mục</option>
                        <c:forEach var="category" items="${requestScope.categories}">
                            <option value="${category.categoryID}" <c:if test="${category.categoryID == selectedCategoryId}">selected</c:if>>${category.categoryName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="brandId" class="form-label visually-hidden">Thương hiệu</label>
                    <select class="form-select" id="brandId" name="brandId">
                        <option value="">Tất cả Thương hiệu</option>
                        <c:forEach var="brand" items="${requestScope.brands}">
                            <option value="${brand.brandID}" <c:if test="${brand.brandID == selectedBrandId}">selected</c:if>>${brand.brandName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="sortBy" class="form-label visually-hidden">Sắp xếp theo</label>
                    <select class="form-select" id="sortBy" name="sortBy">
                        <option value="ProductName" <c:if test="${sortBy == 'ProductName'}">selected</c:if>>Sắp xếp theo Tên</option>
                        <option value="Price" <c:if test="${sortBy == 'Price'}">selected</c:if>>Sắp xếp theo Giá</option>
                        <option value="CreatedDate" <c:if test="${sortBy == 'CreatedDate'}">selected</c:if>>Sắp xếp theo Ngày tạo</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label for="sortOrder" class="form-label visually-hidden">Thứ tự</label>
                    <select class="form-select" id="sortOrder" name="sortOrder">
                        <option value="asc" <c:if test="${sortOrder == 'asc'}">selected</c:if>>Tăng dần</option>
                        <option value="desc" <c:if test="${sortOrder == 'desc'}">selected</c:if>>Giảm dần</option>
                    </select>
                </div>
                <div class="col-12">
                    <button type="submit" class="btn btn-primary w-100">Tìm kiếm & Lọc</button>
                </div>
            </form>
        </div>

        <c:if test="${empty requestScope.productList}">
            <p class="text-center text-muted fs-5 mt-5">Không có sản phẩm nào để hiển thị.</p>
        </c:if>

        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 row-cols-xl-4 g-4">
            <c:forEach var="product" items="${requestScope.productList}">
                <div class="col">
                    <div class="card h-100 product-card shadow-sm">
                        <a href="${pageContext.request.contextPath}/product-detail?id=${product.productID}">
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
                        </a>
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-title text-truncate"><a href="${pageContext.request.contextPath}/product-detail?id=${product.productID}" class="text-decoration-none text-dark">${product.productName}</a></h5>
                            <p class="card-text fs-5 fw-bold text-danger">
                                <fmt:setLocale value="vi_VN"/>
                                <fmt:formatNumber value="${product.price}" type="currency"/>
                            </p>
                            <p class="card-text text-muted mb-1">Danh mục:
                                <c:choose>
                                    <c:when test="${not empty product.category}">
                                        ${product.category.categoryName}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p class="card-text text-muted">Thương hiệu:
                                <c:choose>
                                    <c:when test="${not empty product.brand}">
                                        ${product.brand.brandName}
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <form action="${pageContext.request.contextPath}/cart/add" method="post" class="mt-auto">
                                <input type="hidden" name="productId" value="${product.productID}">
                                <input type="hidden" name="quantity" value="1">
                                <button type="submit" class="btn btn-success w-100">Thêm vào giỏ hàng</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <nav aria-label="Page navigation" class="mt-4">
            <ul class="pagination justify-content-center">
                <li class="page-item <c:if test="${currentPage <= 1}">disabled</c:if>">
                    <c:url var="prevUrl" value="${pageContext.request.contextPath}/products">
                        <c:param name="searchTerm" value="${selectedSearchTerm}"/>
                        <c:param name="categoryId" value="${selectedCategoryId}"/>
                        <c:param name="brandId" value="${selectedBrandId}"/>
                        <c:param name="sortBy" value="${sortBy}"/>
                        <c:param name="sortOrder" value="${sortOrder}"/>
                        <c:param name="page" value="${currentPage - 1}"/>
                        <c:param name="size" value="${pageSize}"/>
                    </c:url>
                    <a class="page-link" href="${prevUrl}" aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item <c:if test="${i == currentPage}">active</c:if>">
                        <c:url var="pageUrl" value="${pageContext.request.contextPath}/products">
                            <c:param name="searchTerm" value="${selectedSearchTerm}"/>
                            <c:param name="categoryId" value="${selectedCategoryId}"/>
                            <c:param name="brandId" value="${selectedBrandId}"/>
                            <c:param name="sortBy" value="${sortBy}"/>
                            <c:param name="sortOrder" value="${sortOrder}"/>
                            <c:param name="page" value="${i}"/>
                            <c:param name="size" value="${pageSize}"/>
                        </c:url>
                        <a class="page-link" href="${pageUrl}">${i}</a>
                    </li>
                </c:forEach>
                <li class="page-item <c:if test="${currentPage >= totalPages}">disabled</c:if>">
                    <c:url var="nextUrl" value="${pageContext.request.contextPath}/products">
                        <c:param name="searchTerm" value="${selectedSearchTerm}"/>
                        <c:param name="categoryId" value="${selectedCategoryId}"/>
                        <c:param name="brandId" value="${selectedBrandId}"/>
                        <c:param name="sortBy" value="${sortBy}"/>
                        <c:param name="sortOrder" value="${sortOrder}"/>
                        <c:param name="page" value="${currentPage + 1}"/>
                        <c:param name="size" value="${pageSize}"/>
                    </c:url>
                    <a class="page-link" href="${nextUrl}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>