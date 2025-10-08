<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <c:if test="${not empty requestScope.order}">
        <h1 class="mb-4">Chi tiết Đơn hàng #${requestScope.order.orderNumber}</h1>

        <c:if test="${not empty param.message}">
            <div class="alert alert-success text-center" role="alert">
                ${param.message}
            </div>
        </c:if>

        <div class="row">
            <div class="col-md-6">
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">Thông tin Đơn hàng</div>
                    <div class="card-body">
                        <p><strong>Mã đơn hàng:</strong> ${requestScope.order.orderNumber}</p>
                        <p><strong>Ngày đặt:</strong> <fmt:formatDate value="${requestScope.order.orderDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></p>
                        <p><strong>Trạng thái đơn hàng:</strong> 
                            <span class="badge 
                                <c:choose>
                                    <c:when test="${requestScope.order.orderStatus == 'Pending'}">bg-warning</c:when>
                                    <c:when test="${requestScope.order.orderStatus == 'Processing'}">bg-info</c:when>
                                    <c:when test="${requestScope.order.orderStatus == 'Shipped'}">bg-primary</c:when>
                                    <c:when test="${requestScope.order.orderStatus == 'Delivered'}">bg-success</c:when>
                                    <c:when test="${requestScope.order.orderStatus == 'Cancelled'}">bg-danger</c:when>
                                </c:choose>
                            ">${requestScope.order.orderStatus}</span>
                        </p>
                        <p><strong>Trạng thái thanh toán:</strong> 
                            <span class="badge 
                                <c:choose>
                                    <c:when test="${requestScope.order.paymentStatus == 'Pending'}">bg-warning</c:when>
                                    <c:when test="${requestScope.order.paymentStatus == 'Paid'}">bg-success</c:when>
                                    <c:when test="${requestScope.order.paymentStatus == 'Refunded'}">bg-danger</c:when>
                                </c:choose>
                            ">${requestScope.order.paymentStatus}</span>
                        </p>
                        <p><strong>Phương thức thanh toán:</strong> ${requestScope.order.paymentMethod}</p>
                        <p><strong>ID Giao dịch thanh toán:</strong> ${requestScope.order.paymentTransactionID}</p>
                        <p><strong>Ghi chú:</strong> ${requestScope.order.notes}</p>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">Thông tin Người dùng</div>
                    <div class="card-body">
                        <p><strong>Người dùng:</strong> ${requestScope.order.user.username}</p>
                        <p><strong>Email:</strong> ${requestScope.order.user.email}</p>
                        <p><strong>Họ và tên:</strong> ${requestScope.order.user.firstName} ${requestScope.order.user.lastName}</p>
                    </div>
                </div>
            </div>
        </div>

        <div class="card shadow-sm mb-4">
            <div class="card-header bg-primary text-white">Sản phẩm trong đơn hàng</div>
            <div class="card-body">
                <div class="table-responsive">
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
            </div>
        </div>

        <div class="row">
            <div class="col-md-6">
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">Địa chỉ giao hàng</div>
                    <div class="card-body">
                        <c:if test="${not empty requestScope.order.shippingAddress}">
                            <p><strong>Loại địa chỉ:</strong> ${requestScope.order.shippingAddress.addressType}</p>
                            <p><strong>Địa chỉ:</strong> ${requestScope.order.shippingAddress.addressLine1}, ${requestScope.order.shippingAddress.addressLine2}</p>
                            <p><strong>Thành phố:</strong> ${requestScope.order.shippingAddress.city}</p>
                            <p><strong>Tỉnh/Bang:</strong> ${requestScope.order.shippingAddress.state}</p>
                            <p><strong>Mã bưu chính:</strong> ${requestScope.order.shippingAddress.postalCode}</p>
                            <p><strong>Quốc gia:</strong> ${requestScope.order.shippingAddress.country}</p>
                        </c:if>
                        <c:if test="${empty requestScope.order.shippingAddress}">
                            <p class="text-muted">Không có thông tin địa chỉ giao hàng.</p>
                        </c:if>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card shadow-sm mb-4">
                    <div class="card-header bg-primary text-white">Địa chỉ thanh toán</div>
                    <div class="card-body">
                        <c:if test="${not empty requestScope.order.billingAddress}">
                            <p><strong>Loại địa chỉ:</strong> ${requestScope.order.billingAddress.addressType}</p>
                            <p><strong>Địa chỉ:</strong> ${requestScope.order.billingAddress.addressLine1}, ${requestScope.order.billingAddress.addressLine2}</p>
                            <p><strong>Thành phố:</strong> ${requestScope.order.billingAddress.city}</p>
                            <p><strong>Tỉnh/Bang:</strong> ${requestScope.order.billingAddress.state}</p>
                            <p><strong>Mã bưu chính:</strong> ${requestScope.order.billingAddress.postalCode}</p>
                            <p><strong>Quốc gia:</strong> ${requestScope.order.billingAddress.country}</p>
                        </c:if>
                        <c:if test="${empty requestScope.order.billingAddress}">
                            <p class="text-muted">Không có thông tin địa chỉ thanh toán.</p>
                        </c:if>
                    </div>
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
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay lại danh sách đơn hàng</a>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />