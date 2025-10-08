<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Giỏ Hàng Của Bạn</title>
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
        .product-image {
            width: 80px;
            height: 80px;
            object-fit: contain;
            margin-right: 10px;
            vertical-align: middle;
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

    <div class="container mt-4 mb-5">
        <h1 class="text-center mb-4">Giỏ Hàng Của Bạn</h1>

        <c:if test="${requestScope.cartEmpty}">
            <div class="alert alert-info text-center" role="alert">
                Giỏ hàng của bạn đang trống. <a href="${pageContext.request.contextPath}/products" class="alert-link">Tiếp tục mua sắm</a>.
            </div>
        </c:if>

        <c:if test="${!requestScope.cartEmpty}">
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Giá</th>
                            <th>Số lượng</th>
                            <th>Tổng cộng</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="cartTotal" value="0" />
                        <c:forEach var="item" items="${requestScope.cartItems}">
                            <c:set var="itemTotalPrice" value="${item.product.price * item.quantity}" />
                            <c:set var="cartTotal" value="${cartTotal + itemTotalPrice}" />
                            <tr>
                                <td>
                                    <img src="https://via.placeholder.com/80x80?text=Product" alt="${item.product.productName}" class="product-image rounded">
                                    <span class="product-name fw-bold">${item.product.productName}</span>
                                </td>
                                <td>
                                    <fmt:setLocale value="vi_VN"/>
                                    <fmt:formatNumber value="${item.product.price}" type="currency"/>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/update" method="post" class="d-flex align-items-center">
                                        <input type="hidden" name="productId" value="${item.product.productID}">
                                        <input type="number" name="quantity" value="${item.quantity}" min="1" class="form-control quantity-input me-2">
                                        <button type="submit" class="btn btn-sm btn-primary">Cập nhật</button>
                                    </form>
                                </td>
                                <td>
                                    <fmt:setLocale value="vi_VN"/>
                                    <fmt:formatNumber value="${itemTotalPrice}" type="currency"/>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/cart/remove" method="get">
                                        <input type="hidden" name="productId" value="${item.product.productID}">
                                        <button type="submit" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i> Xóa</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>

            <div class="row justify-content-end mt-4">
                <div class="col-md-4">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">Tổng cộng giỏ hàng</h5>
                            <p class="card-text fs-4 fw-bold text-danger">
                                <fmt:setLocale value="vi_VN"/>
                                <fmt:formatNumber value="${cartTotal}" type="currency"/>
                            </p>
                            <div class="d-grid gap-2">
                                <form action="${pageContext.request.contextPath}/cart/clear" method="get">
                                    <button type="submit" class="btn btn-warning w-100"><i class="fas fa-trash-alt"></i> Xóa toàn bộ giỏ hàng</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-success w-100"><i class="fas fa-money-check-alt"></i> Tiến hành thanh toán</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>