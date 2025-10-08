<%@ page import="dto.ProductReviewResponseDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.productName} - Chi tiết sản phẩm</title>
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
        .product-image img {
            max-width: 100%;
            height: auto;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .thumbnail-gallery img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border: 1px solid #ddd;
            border-radius: 3px;
            cursor: pointer;
        }
        .thumbnail-gallery img.active {
            border-color: #007bff;
            border-width: 2px;
        }
        .star-rating input[type="radio"] {
            display: none;
        }
        .star-rating label {
            font-size: 1.5em;
            color: #ccc;
            cursor: pointer;
        }
        .star-rating label:hover, .star-rating label:hover ~ label,
        .star-rating input[type="radio"]:checked ~ label {
            color: #ffc107;
        }
        .review-item {
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            background-color: #ffffff;
            box-shadow: 0 2px 5px rgba(0,0,0,.05);
        }
        .review-item .review-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;
        }
        .review-item .reviewer-info {
            font-size: 1.1em;
            color: #333;
        }
        .review-item .review-date {
            font-size: 0.9em;
            color: #777;
        }
        .review-item .review-rating {
            font-size: 1.3em;
            color: #ffc107; /* Star color */
            margin-bottom: 8px;
        }
        .review-item .review-title {
            font-size: 1.1em;
            font-weight: 600;
            color: #555;
            margin-bottom: 5px;
        }
        .review-item .review-text {
            color: #666;
            line-height: 1.6;
        }
        .verified-badge {
            background-color: #28a745;
            color: white;
            padding: 3px 8px;
            border-radius: 12px;
            font-size: 0.75em;
            font-weight: bold;
            margin-left: 10px;
        }
        .footer {
            background-color: #343a40; /* Dark background */
            color: #ffffff; /* White text */
            padding: 20px 0;
            text-align: center;
            margin-top: 40px; /* Add some space above the footer */
        }
        .footer a {
            color: #cccccc;
            text-decoration: none;
        }
        .footer a:hover {
            color: #ffffff;
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        <c:if test="${not empty requestScope.product}">
            <h1 class="text-center mb-4">${requestScope.product.productName}</h1>
            <div class="row product-detail">
                <div class="col-md-5 product-image">
                    <c:set var="mainImage" value="${null}"/>
                    <c:forEach var="img" items="${requestScope.product.images}">
                        <c:if test="${img.mainImage}">
                            <c:set var="mainImage" value="${img}"/>
                        </c:if>
                    </c:forEach>

                    <c:choose>
                        <c:when test="${not empty mainImage}">
                            <img id="mainProductImage" src="${pageContext.request.contextPath}/static/images/${mainImage.imageURL.substring(mainImage.imageURL.lastIndexOf('/') + 1)}" class="img-fluid rounded shadow-sm" alt="${requestScope.product.productName}">
                        </c:when>
                        <c:otherwise>
                            <img id="mainProductImage" src="https://via.placeholder.com/300x300?text=No+Image" class="img-fluid rounded shadow-sm" alt="${requestScope.product.productName}">
                        </c:otherwise>
                    </c:choose>

                    <div class="thumbnail-gallery d-flex justify-content-center gap-2 mt-3">
                        <c:forEach var="img" items="${requestScope.product.images}">
                            <img src="${pageContext.request.contextPath}/static/images/${img.imageURL.substring(img.imageURL.lastIndexOf('/') + 1)}" alt="${img.altText}" 
                                 onclick="changeMainImage(this)" class="img-thumbnail ${img.mainImage ? 'active' : ''}">
                        </c:forEach>
                    </div>
                </div>
                <div class="col-md-7 product-info">
                    <h2 class="text-primary">${requestScope.product.productName}</h2>
                    <p class="price fs-3 fw-bold text-danger">
                        <fmt:setLocale value="vi_VN"/>
                        <fmt:formatNumber value="${requestScope.product.price}" type="currency"/>
                    </p>
                    <p><span class="fw-bold text-secondary">Mô tả ngắn:</span> ${requestScope.product.shortDescription}</p>
                    <p><span class="fw-bold text-secondary">Mô tả chi tiết:</span> ${requestScope.product.description}</p>
                    <p><span class="fw-bold text-secondary">SKU:</span> ${requestScope.product.sku}</p>
                    <p><span class="fw-bold text-secondary">Danh mục:</span> 
                        <c:choose>
                            <c:when test="${not empty requestScope.product.category}">
                                ${requestScope.product.category.categoryName}
                            </c:when>
                            <c:otherwise>
                                N/A
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p><span class="fw-bold text-secondary">Thương hiệu:</span> 
                        <c:choose>
                            <c:when test="${not empty requestScope.product.brand}">
                                ${requestScope.product.brand.brandName}
                            </c:when>
                            <c:otherwise>
                                N/A
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <p><span class="fw-bold text-secondary">Số lượng tồn kho:</span> ${requestScope.product.stockQuantity}</p>
                    <p><span class="fw-bold text-secondary">Đánh giá trung bình:</span> ${requestScope.product.averageRating} (${requestScope.product.reviewCount} đánh giá)</p>
                    <p><span class="fw-bold text-secondary">Kích thước:</span> ${requestScope.product.dimensions}</p>
                    <p><span class="fw-bold text-secondary">Cân nặng:</span> ${requestScope.product.weight} kg</p>
                    <p><span class="fw-bold text-secondary">Ngày tạo:</span> <fmt:formatDate value="${requestScope.product.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                    <p><span class="fw-bold text-secondary">Ngày cập nhật:</span> <fmt:formatDate value="${requestScope.product.modifiedDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                    
                    <form action="${pageContext.request.contextPath}/cart/add" method="post" class="d-flex align-items-center mt-4">
                        <input type="hidden" name="productId" value="${requestScope.product.productID}">
                        <label for="quantity" class="form-label me-2 mb-0">Số lượng:</label>
                        <input type="number" id="quantity" name="quantity" value="1" min="1" class="form-control w-auto me-3">
                        <button type="submit" class="btn btn-success">Thêm vào giỏ hàng</button>
                    </form>
                </div>
            </div>

            <div class="review-section mt-5 pt-4 border-top">
                <h2 class="mb-4">Đánh giá Sản phẩm</h2>

                <c:if test="${not empty param.message}">
                    <div class="alert alert-success" role="alert">
                        ${param.message}
                    </div>
                </c:if>
                <c:if test="${not empty requestScope.errorMessage}">
                    <div class="alert alert-danger" role="alert">
                        ${requestScope.errorMessage}
                    </div>
                </c:if>

                <c:if test="${not empty sessionScope.user}">
                    <div class="review-form p-4 bg-light rounded shadow-sm mb-4">
                        <h3>Gửi đánh giá của bạn</h3>
                        <form action="${pageContext.request.contextPath}/product/review/add" method="post">
                            <input type="hidden" name="productId" value="${requestScope.product.productID}">
                            <div class="mb-3">
                                <label class="form-label">Điểm đánh giá:</label>
                                <div class="star-rating">
                                    <input type="radio" id="star5" name="rating" value="5" required><label for="star5" title="5 sao">&#9733;</label>
                                    <input type="radio" id="star4" name="rating" value="4"><label for="star4" title="4 sao">&#9733;</label>
                                    <input type="radio" id="star3" name="rating" value="3"><label for="star3" title="3 sao">&#9733;</label>
                                    <input type="radio" id="star2" name="rating" value="2"><label for="star2" title="2 sao">&#9733;</label>
                                    <input type="radio" id="star1" name="rating" value="1"><label for="star1" title="1 sao">&#9733;</label>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label for="reviewTitle" class="form-label">Tiêu đề:</label>
                                <input type="text" class="form-control" id="reviewTitle" name="title" placeholder="Tóm tắt đánh giá của bạn">
                            </div>
                            <div class="mb-3">
                                <label for="reviewText" class="form-label">Nội dung đánh giá:</label>
                                <textarea class="form-control" id="reviewText" name="reviewText" rows="5" placeholder="Viết đánh giá chi tiết của bạn..."></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
                        </form>
                    </div>
                </c:if>
                <c:if test="${empty sessionScope.user}">
                    <p class="text-center text-muted">Vui lòng <a href="${pageContext.request.contextPath}/login">đăng nhập</a> để gửi đánh giá.</p>
                </c:if>

                <h3 class="mb-3">Các đánh giá khác (<%= request.getAttribute("product") != null ? ((model.Product)request.getAttribute("product")).getReviewCount() : 0 %>)</h3>
                <% 
                    List<ProductReviewResponseDTO> publishedReviews = (List<ProductReviewResponseDTO>) request.getAttribute("publishedReviews");
                    if (publishedReviews == null || publishedReviews.isEmpty()) {
                %>
                        <p class="text-center text-muted">Chưa có đánh giá nào cho sản phẩm này.</p>
                <% 
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        for (ProductReviewResponseDTO review : publishedReviews) {
                %>
                            <div class="review-item">
                                <div class="review-header">
                                    <span class="reviewer-info">
                                        <%= review.getUserFullName() %> (<%= review.getUsername() %>)
                                        <% if (review.isVerifiedPurchase()) { %>
                                            <span class="verified-badge">Đã xác minh mua hàng</span>
                                        <% } %>
                                    </span>
                                    <span class="review-date"><%= sdf.format(review.getCreatedDateAsDate()) %></span>
                                </div>
                                <div class="review-rating">
                                    <% for (int i = 1; i <= 5; i++) { %>
                                        <% if (i <= review.getRating()) { %>&#9733;<% } else { %>&#9734;<% } %>
                                    <% } %>
                                </div>
                                <p class="review-title"><%= review.getTitle() %></p>
                                <p class="review-text"><%= review.getReviewText() %></p>
                            </div>
                <% 
                        }
                    }
                %>
            </div>
        </c:if>
        <c:if test="${empty requestScope.product}">
            <div class="alert alert-danger text-center" role="alert">
                Không tìm thấy thông tin sản phẩm.
            </div>
        </c:if>
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">Quay lại danh sách sản phẩm</a>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

    <script>
        function changeMainImage(thumbnail) {
            var mainImage = document.getElementById('mainProductImage');
            mainImage.src = thumbnail.src;

            // Remove active class from all thumbnails
            var thumbnails = document.querySelectorAll('.thumbnail-gallery img');
            thumbnails.forEach(function(img) {
                img.classList.remove('active');
            });

            // Add active class to the clicked thumbnail
            thumbnail.classList.add('active');
        }
    </script>
</body>
</html>