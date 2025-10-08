<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử Đơn hàng của bạn</title>
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
        .order-card {
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 20px;
            background-color: #fff;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        .order-card h3 {
            color: #007bff;
            margin-bottom: 15px;
        }
        .order-card .status {
            font-weight: bold;
        }
        .status.pending { color: #ffc107; }
        .status.processing { color: #17a2b8; }
        .status.shipped { color: #007bff; }
        .status.delivered { color: #28a745; }
        .status.cancelled { color: #dc3545; }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        <h1 class="text-center mb-4">Lịch sử Đơn hàng của bạn</h1>

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

        <c:if test="${empty requestScope.orders}">
            <div class="alert alert-info text-center" role="alert">
                Bạn chưa có đơn hàng nào.
            </div>
        </c:if>

        <div class="row row-cols-1 g-3">
            <c:forEach var="order" items="${requestScope.orders}">
                <div class="col">
                    <div class="order-card">
                        <h3 class="mb-3">Đơn hàng #${order.orderNumber}</h3>
                        <p><strong>Ngày đặt:</strong> <fmt:formatDate value="${order.orderDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                        <p><strong>Tổng cộng:</strong> 
                            <fmt:setLocale value="vi_VN"/>
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/>
                        </p>
                        <p><strong>Trạng thái đơn hàng:</strong> 
                            <span class="status 
                                <c:choose>
                                    <c:when test="${order.orderStatus == 'Pending'}">pending</c:when>
                                    <c:when test="${order.orderStatus == 'Processing'}">processing</c:when>
                                    <c:when test="${order.orderStatus == 'Shipped'}">shipped</c:when>
                                    <c:when test="${order.orderStatus == 'Delivered'}">delivered</c:when>
                                    <c:when test="${order.orderStatus == 'Cancelled'}">cancelled</c:when>
                                </c:choose>
                            ">${order.orderStatus}</span>
                        </p>
                        <p><strong>Trạng thái thanh toán:</strong> <span class="status 
                                <c:choose>
                                    <c:when test="${order.paymentStatus == 'Pending'}">pending</c:when>
                                    <c:when test="${order.paymentStatus == 'Paid'}">delivered</c:when>
                                    <c:when test="${order.paymentStatus == 'Refunded'}">cancelled</c:when>
                                </c:choose>
                            ">${order.paymentStatus}</span></p>
                        <a href="${pageContext.request.contextPath}/orders/detail?id=${order.orderID}" class="btn btn-secondary mt-3">Xem chi tiết</a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>