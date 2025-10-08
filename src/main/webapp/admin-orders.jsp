<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Đơn hàng</h1>

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

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID Đơn hàng</th>
                    <th>Người dùng</th>
                    <th>Ngày đặt</th>
                    <th>Tổng cộng</th>
                    <th>Trạng thái Đơn hàng</th>
                    <th>Trạng thái Thanh toán</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty requestScope.orderList}">
                    <tr>
                        <td colspan="7" class="text-center text-muted">Không có đơn hàng nào để hiển thị.</td>
                    </tr>
                </c:if>
                <c:forEach var="order" items="${requestScope.orderList}">
                    <tr>
                        <td>${order.orderID}</td>
                        <td>${order.user.username}</td>
                        <td><fmt:formatDate value="${order.orderDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫"/></td>
                        <td>
                            <span class="badge 
                                <c:choose>
                                    <c:when test="${order.orderStatus == 'Pending'}">bg-warning</c:when>
                                    <c:when test="${order.orderStatus == 'Processing'}">bg-info</c:when>
                                    <c:when test="${order.orderStatus == 'Shipped'}">bg-primary</c:when>
                                    <c:when test="${order.orderStatus == 'Delivered'}">bg-success</c:when>
                                    <c:when test="${order.orderStatus == 'Cancelled'}">bg-danger</c:when>
                                </c:choose>
                            ">${order.orderStatus}</span>
                        </td>
                        <td>
                            <span class="badge 
                                <c:choose>
                                    <c:when test="${order.paymentStatus == 'Pending'}">bg-warning</c:when>
                                    <c:when test="${order.paymentStatus == 'Paid'}">bg-success</c:when>
                                    <c:when test="${order.paymentStatus == 'Refunded'}">bg-danger</c:when>
                                </c:choose>
                            ">${order.paymentStatus}</span>
                            <form action="${pageContext.request.contextPath}/admin/order/updatePaymentStatus" method="post" style="display:inline-block; margin-left: 5px;">
                                <input type="hidden" name="action" value="updatePaymentStatus">
                                <input type="hidden" name="orderId" value="${order.orderID}">
                                <select name="newPaymentStatus" class="form-select form-select-sm d-inline-block w-auto" onchange="this.form.submit()">
                                    <option value="">Cập nhật</option>
                                    <option value="Pending" <c:if test="${order.paymentStatus eq 'Pending'}">selected</c:if>>Chờ xử lý</option>
                                    <option value="Paid" <c:if test="${order.paymentStatus eq 'Paid'}">selected</c:if>>Đã thanh toán</option>
                                    <option value="Refunded" <c:if test="${order.paymentStatus eq 'Refunded'}">selected</c:if>>Đã hoàn tiền</option>
                                </select>
                            </form>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/orders/detail?id=${order.orderID}" class="btn btn-sm btn-info text-white me-1"><i class="fas fa-info-circle"></i> Chi tiết</a>
                            <a href="${pageContext.request.contextPath}/admin/orders/edit?id=${order.orderID}" class="btn btn-sm btn-warning me-1"><i class="fas fa-edit"></i> Sửa</a>
                            <c:if test="${order.orderStatus eq 'Pending'}">
                                <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.orderID}">
                                    <input type="hidden" name="newStatus" value="Processing">
                                    <button type="submit" class="btn btn-info btn-sm mt-1"><i class="fas fa-cogs"></i> Xử lý</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.orderID}">
                                    <input type="hidden" name="newStatus" value="Cancelled">
                                    <button type="submit" class="btn btn-danger btn-sm mt-1"><i class="fas fa-times-circle"></i> Hủy</button>
                                </form>
                            </c:if>
                            <c:if test="${order.orderStatus eq 'Processing'}">
                                <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.orderID}">
                                    <input type="hidden" name="newStatus" value="Shipped">
                                    <button type="submit" class="btn btn-primary btn-sm mt-1"><i class="fas fa-truck"></i> Giao hàng</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.orderID}">
                                    <input type="hidden" name="newStatus" value="Cancelled">
                                    <button type="submit" class="btn btn-danger btn-sm mt-1"><i class="fas fa-times-circle"></i> Hủy</button>
                                </form>
                            </c:if>
                            <c:if test="${order.orderStatus eq 'Shipped'}">
                                <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="updateStatus">
                                    <input type="hidden" name="orderId" value="${order.orderID}">
                                    <input type="hidden" name="newStatus" value="Delivered">
                                    <button type="submit" class="btn btn-success btn-sm mt-1"><i class="fas fa-check-circle"></i> Hoàn thành</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />