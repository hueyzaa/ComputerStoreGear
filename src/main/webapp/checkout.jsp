<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh Toán</title>
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
        .section-title {
            color: #007bff;
            border-bottom: 1px solid #eee;
            padding-bottom: 10px;
            margin-top: 30px;
            margin-bottom: 20px;
        }
        .address-item {
            border: 1px solid #e9ecef;
            padding: 15px;
            margin-bottom: 10px;
            border-radius: 8px;
            background-color: #fff;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        .address-item input[type="radio"] {
            margin-right: 10px;
        }
        .payment-method label {
            font-weight: bold;
        }
        .payment-details-section {
            background-color: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
            margin-top: 20px;
        }
    </style>
</head>
<body>

    <jsp:include page="header.jsp" />

    <div class="container mt-4 mb-5">
        <h1 class="text-center mb-4">Thanh Toán</h1>

        <c:if test="${not empty requestScope.errorMessage}">
            <div class="alert alert-danger text-center" role="alert">
                ${requestScope.errorMessage}
            </div>
        </c:if>

        <div class="row">
            <div class="col-md-8">
                <h2 class="section-title">Tóm tắt đơn hàng</h2>
                <div class="table-responsive mb-4">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>Sản phẩm</th>
                                <th>Số lượng</th>
                                <th>Giá đơn vị</th>
                                <th>Tổng cộng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${requestScope.cartItems}">
                                <tr>
                                    <td>${item.product.productName}</td>
                                    <td>${item.quantity}</td>
                                    <td><fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="₫"/></td>
                                    <td><fmt:formatNumber value="${item.product.price * item.quantity}" type="currency" currencySymbol="₫"/></td>
                                </tr>
                            </c:forEach>
                            <tr class="table-light fw-bold">
                                <td colspan="3">Tổng phụ:</td>
                                <td><fmt:formatNumber value="${requestScope.subtotal}" type="currency" currencySymbol="₫"/></td>
                            </tr>
                            <c:if test="${not empty requestScope.discountAmount and requestScope.discountAmount gt 0}">
                                <tr class="table-light fw-bold">
                                    <td colspan="3">Giảm giá (<c:out value="${requestScope.appliedCouponCode}"/>):</td>
                                    <td>- <fmt:formatNumber value="${requestScope.discountAmount}" type="currency" currencySymbol="₫"/></td>
                                </tr>
                            </c:if>
                            <tr class="table-primary fw-bold fs-5">
                                <td colspan="3">Tổng cộng:</td>
                                <td><fmt:formatNumber value="${requestScope.totalAmount}" type="currency" currencySymbol="₫"/></td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div class="coupon-section p-4 bg-light rounded shadow-sm mb-4">
                    <h3 class="mb-3">Mã giảm giá</h3>
                    <form action="${pageContext.request.contextPath}/checkout" method="post" class="row g-3 align-items-center">
                        <div class="col-auto">
                            <input type="text" class="form-control" name="couponCode" placeholder="Nhập mã giảm giá" value="${requestScope.appliedCouponCode}">
                        </div>
                        <div class="col-auto">
                            <button type="submit" name="action" value="applyCoupon" class="btn btn-primary">Áp dụng</button>
                        </div>
                        <c:if test="${not empty requestScope.appliedCouponCode}">
                            <div class="col-auto">
                                <button type="submit" name="action" value="removeCoupon" class="btn btn-outline-danger">Xóa mã</button>
                            </div>
                        </c:if>
                    </form>
                    <c:if test="${not empty requestScope.couponMessage}">
                        <p class="coupon-message mt-3 <c:if test="${requestScope.couponSuccess}">text-success</c:if><c:if test="${!requestScope.couponSuccess}">text-danger</c:if>">${requestScope.couponMessage}</p>
                    </c:if>
                </div>

                <form action="${pageContext.request.contextPath}/checkout/place-order" method="post">
                    <c:if test="${not empty requestScope.appliedCouponCode}">
                        <input type="hidden" name="appliedCouponCode" value="${requestScope.appliedCouponCode}">
                        <input type="hidden" name="discountAmount" value="${requestScope.discountAmount}">
                    </c:if>

                    <h2 class="section-title">Địa chỉ giao hàng</h2>
                    <div class="address-selection mb-4">
                        <c:if test="${empty requestScope.addresses}">
                            <div class="alert alert-warning" role="alert">
                                Bạn chưa có địa chỉ nào. Vui lòng <a href="${pageContext.request.contextPath}/profile/addresses/add" class="alert-link">thêm địa chỉ mới</a>.
                            </div>
                        </c:if>
                        <c:forEach var="address" items="${requestScope.addresses}">
                            <div class="form-check address-item">
                                <input class="form-check-input" type="radio" id="shipping-${address.addressID}" name="shippingAddressId" value="${address.addressID}" 
                                       <c:if test="${address.isDefault}">checked</c:if>>
                                <label class="form-check-label" for="shipping-${address.addressID}">
                                    <strong>${address.addressType}</strong>: ${address.addressLine1}, ${address.addressLine2} ${address.city}, ${address.state} ${address.postalCode}, ${address.country}
                                    <c:if test="${address.isDefault}"> <span class="badge bg-info">Mặc định</span></c:if>
                                </label>
                            </div>
                        </c:forEach>
                        <div class="text-center mt-3"><a href="${pageContext.request.contextPath}/profile/addresses/add" class="btn btn-outline-primary">Thêm địa chỉ mới</a></div>
                    </div>

                    <h2 class="section-title">Địa chỉ thanh toán</h2>
                    <div class="address-selection mb-4">
                        <c:if test="${empty requestScope.addresses}">
                            <div class="alert alert-warning" role="alert">
                                Bạn chưa có địa chỉ nào. Vui lòng <a href="${pageContext.request.contextPath}/profile/addresses/add" class="alert-link">thêm địa chỉ mới</a>.
                            </div>
                        </c:if>
                        <c:forEach var="address" items="${requestScope.addresses}">
                            <div class="form-check address-item">
                                <input class="form-check-input" type="radio" id="billing-${address.addressID}" name="billingAddressId" value="${address.addressID}" 
                                       <c:if test="${address.isDefault}">checked</c:if>>
                                <label class="form-check-label" for="billing-${address.addressID}">
                                    <strong>${address.addressType}</strong>: ${address.addressLine1}, ${address.addressLine2} ${address.city}, ${address.state} ${address.postalCode}, ${address.country}
                                    <c:if test="${address.isDefault}"> <span class="badge bg-info">Mặc định</span></c:if>
                                </label>
                            </div>
                        </c:forEach>
                        <div class="text-center mt-3"><a href="${pageContext.request.contextPath}/profile/addresses/add" class="btn btn-outline-primary">Thêm địa chỉ mới</a></div>
                    </div>

                    <h2 class="section-title">Phương thức thanh toán</h2>
                    <div class="payment-method mb-4">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" id="cod" name="paymentMethod" value="COD" checked>
                            <label class="form-check-label" for="cod">Thanh toán khi nhận hàng (COD)</label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" id="card" name="paymentMethod" value="Card">
                            <label class="form-check-label" for="card">Thẻ tín dụng/ghi nợ</label>
                        </div>
                    </div>

                    <div class="payment-details-section" id="cardPaymentDetails" style="display: none;">
                        <h3 class="mb-3">Thông tin thẻ (Mô phỏng)</h3>
                        <div class="mb-3">
                            <label for="cardNumber" class="form-label">Số thẻ:</label>
                            <input type="text" class="form-control" id="cardNumber" name="cardNumber" placeholder="XXXX XXXX XXXX XXXX" pattern="[0-9]{13,19}" title="Số thẻ phải có từ 13 đến 19 chữ số">
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="expiryDate" class="form-label">Ngày hết hạn (MM/YY):</label>
                                <input type="text" class="form-control" id="expiryDate" name="expiryDate" placeholder="MM/YY" pattern="(0[1-9]|1[0-2])\/[0-9]{2}" title="Ngày hết hạn phải ở định dạng MM/YY">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="cvv" class="form-label">CVV:</label>
                                <input type="text" class="form-control" id="cvv" name="cvv" placeholder="XXX" pattern="[0-9]{3,4}" title="CVV phải có 3 hoặc 4 chữ số">
                            </div>
                        </div>
                    </div>

                    <h2 class="section-title">Ghi chú đơn hàng (Tùy chọn)</h2>
                    <div class="mb-4">
                        <label for="notes" class="form-label">Ghi chú:</label>
                        <textarea class="form-control" id="notes" name="notes" rows="4" placeholder="Ghi chú về đơn hàng của bạn..."></textarea>
                    </div>

                    <button type="submit" class="btn btn-success btn-lg w-100">Đặt hàng</button>
                </form>
            </div>
            <div class="col-md-4">
                <!-- Có thể thêm tóm tắt đơn hàng hoặc quảng cáo ở đây -->
            </div>
        </div>
    </div>

    <jsp:include page="footer.jsp" />

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var codRadio = document.getElementById('cod');
            var cardRadio = document.getElementById('card');
            var cardPaymentDetails = document.getElementById('cardPaymentDetails');

            function toggleCardDetails() {
                if (cardRadio.checked) {
                    cardPaymentDetails.style.display = 'block';
                } else {
                    cardPaymentDetails.style.display = 'none';
                }
            }

            codRadio.addEventListener('change', toggleCardDetails);
            cardRadio.addEventListener('change', toggleCardDetails);

            // Initial check
            toggleCardDetails();
        });
    </script>
</body>
</html>