<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết Đơn hàng #${order.orderNumber}</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        html, body {
            height: 100%;
        }
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
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
            flex-grow: 1;
            padding: 20px 0; /* Add some vertical padding */
        }
        .container {
            margin: 0 auto; /* Center the container horizontally */
            max-width: 960px; /* Limit width for better readability on large screens */
            /* Add some top/bottom margin for spacing */
            margin-top: 20px;
            margin-bottom: 20px;
        }
        .footer {
            background-color: #212529;
            color: white;
            padding: 30px 0;
            text-align: center;
            margin-top: auto; /* Push footer to the bottom */
        }
        .footer a {
            color: #cccccc;
            text-decoration: none;
        }
        .footer a:hover {
            color: #ffffff;
        }
        .section-title {
            color: #007bff;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
            margin-top: 30px;
            margin-bottom: 20px;
        }
        .order-info p {
            margin: 5px 0;
        }
        .order-info .label {
            font-weight: bold;
            color: #555;
        }
        .address-box {
            border: 1px solid #e9ecef;
            padding: 15px;
            border-radius: 8px;
            margin-top: 10px;
            background-color: #fff;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        .status {
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

    <div class="main-content">
        <div class="container mt-4 mb-5">
        <c:if test="${not empty requestScope.order}">
            <h1 class="text-center mb-4">Chi tiết Đơn hàng #${requestScope.order.orderNumber}</h1>

            <c:if test="${not empty param.message}">
                <div class="alert alert-success text-center" role="alert">
                    ${param.message}
                </div>
            </c:if>

            <h2 class="section-title">Thông tin Đơn hàng</h2>
            <div class="order-info mb-4 p-3 bg-light rounded shadow-sm">
                <p><span class="label">Mã đơn hàng:</span> ${requestScope.order.orderNumber}</p>
                <p><span class="label">Ngày đặt:</span> <fmt:formatDate value="${requestScope.orderDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                <p><span class="label">Trạng thái đơn hàng:</span> 
                    <span class="status 
                        <c:choose>
                            <c:when test="${requestScope.order.orderStatus == 'Pending'}">pending</c:when>
                            <c:when test="${requestScope.order.orderStatus == 'Processing'}">processing</c:when>
                            <c:when test="${requestScope.order.orderStatus == 'Shipped'}">shipped</c:when>
                            <c:when test="${requestScope.order.orderStatus == 'Delivered'}">delivered</c:when>
                            <c:when test="${requestScope.order.orderStatus == 'Cancelled'}">cancelled</c:when>
                        </c:choose>
                    ">${requestScope.order.orderStatus}</span>
                </p>
                <p><span class="label">Trạng thái thanh toán:</span> 
                    <span class="status 
                        <c:choose>
                            <c:when test="${requestScope.order.paymentStatus == 'Pending'}">pending</c:when>
                            <c:when test="${requestScope.order.paymentStatus == 'Paid'}">delivered</c:when>
                            <c:when test="${requestScope.order.paymentStatus == 'Refunded'}">cancelled</c:when>
                        </c:choose>
                    ">${requestScope.order.paymentStatus}</span>
                </p>
                <p><span class="label">Phương thức thanh toán:</span> ${requestScope.order.paymentMethod}</p>
                <p><span class="label">ID Giao dịch thanh toán:</span> ${requestScope.order.paymentTransactionID}</p>
                <p><span class="label">Ghi chú:</span> ${requestScope.order.notes}</p>
            </div>

            <h2 class="section-title">Sản phẩm trong đơn hàng</h2>
            <c:if test="${not empty requestScope.order.orderItems}">
                <div class="table-responsive mb-4">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Sản phẩm</th>
                                <th>SKU</th>
                                <th>Số lượng</th>
                                <th>Giá đơn vị</th>
                                <th>Tổng cộng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${requestScope.order.orderItems}">
                                <tr>
                                    <td>${item.productName}</td>
                                    <td>${item.sku}</td>
                                    <td>${item.quantity}</td>
                                    <td>
                                        <fmt:setLocale value="vi_VN"/>
                                        <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫"/>
                                    </td>
                                    <td>
                                        <fmt:setLocale value="vi_VN"/>
                                        <fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="₫"/>
                                    </td>
                                </tr>
                            </c:forEach>
                            <tr class="table-light fw-bold">
                                <td colspan="4">Tổng phụ:</td>
                                <td>
                                    <fmt:setLocale value="vi_VN"/>
                                    <fmt:formatNumber value="${requestScope.order.subtotalAmount}" type="currency" currencySymbol="₫"/>
                                </td>
                            </tr>
                            <tr class="table-light fw-bold">
                                <td colspan="4">Giảm giá:</td>
                                <td>
                                    <fmt:setLocale value="vi_VN"/>
                                    <fmt:formatNumber value="${requestScope.order.discountAmount}" type="currency" currencySymbol="₫"/>
                                </td>
                            </tr>
                            <tr class="table-primary fw-bold fs-5">
                                <td colspan="4">Tổng cộng:</td>
                                <td>
                                    <fmt:setLocale value="vi_VN"/>
                                    <fmt:formatNumber value="${requestScope.order.totalAmount}" type="currency" currencySymbol="₫"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <c:if test="${empty requestScope.order.orderItems}">
                <div class="alert alert-warning" role="alert">
                    Không có sản phẩm nào trong đơn hàng này.
                </div>
            </c:if>

            <div class="row">
                <div class="col-md-6">
                    <h2 class="section-title">Địa chỉ giao hàng</h2>
                    <div class="address-box">
                        <c:if test="${not empty requestScope.order.shippingAddress}">
                            <p><span class="label">Loại địa chỉ:</span> ${requestScope.order.shippingAddress.addressType}</p>
                            <p><span class="label">Địa chỉ:</span> ${requestScope.order.shippingAddress.addressLine1}<c:if test="${not empty requestScope.order.shippingAddress.addressLine2}">, ${requestScope.order.shippingAddress.addressLine2}</c:if></p>
                            <p><span class="label">Thành phố:</span> ${requestScope.order.shippingAddress.city}</p>
                            <p><span class="label">Tỉnh/Bang:</span> ${requestScope.order.shippingAddress.state}</p>
                            <p><span class="label">Mã bưu chính:</span> ${requestScope.order.shippingAddress.postalCode}</p>
                            <p><span class="label">Quốc gia:</span> ${requestScope.order.shippingAddress.country}</p>
                        </c:if>
                        <c:if test="${empty requestScope.order.shippingAddress}">
                            <p class="text-muted">Không có thông tin địa chỉ giao hàng.</p>
                        </c:if>
                    </div>
                </div>
                <div class="col-md-6">
                    <h2 class="section-title">Địa chỉ thanh toán</h2>
                    <div class="address-box">
                        <c:if test="${not empty requestScope.order.billingAddress}">
                            <p><span class="label">Loại địa chỉ:</span> ${requestScope.order.billingAddress.addressType}</p>
                            <p><span class="label">Địa chỉ:</span> ${requestScope.order.billingAddress.addressLine1}<c:if test="${not empty requestScope.order.billingAddress.addressLine2}">, ${requestScope.order.billingAddress.addressLine2}</c:if></p>
                            <p><span class="label">Thành phố:</span> ${requestScope.order.billingAddress.city}</p>
                            <p><span class="label">Tỉnh/Bang:</span> ${requestScope.order.billingAddress.state}</p>
                            <p><span class="label">Mã bưu chính:</span> ${requestScope.order.billingAddress.postalCode}</p>
                            <p><span class="label">Quốc gia:</span> ${requestScope.order.billingAddress.country}</p>
                        </c:if>
                        <c:if test="${empty requestScope.order.billingAddress}">
                            <p class="text-muted">Không có thông tin địa chỉ thanh toán.</p>
                        </c:if>
                    </div>
                </div>
            </div>

        </c:if>
        <c:if test="${empty requestScope.order}">
            <div class="alert alert-danger text-center" role="alert">
                Không tìm thấy đơn hàng.
            </div>
        </c:if>
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/orders" class="btn btn-secondary">Quay lại lịch sử đơn hàng</a>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

</body>
</html>