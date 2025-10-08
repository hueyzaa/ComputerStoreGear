<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Quản lý Mã giảm giá</h1>

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

    <div class="d-flex justify-content-between align-items-center mb-3">
        <a href="${pageContext.request.contextPath}/admin/coupons/add" class="btn btn-success"><i class="fas fa-plus-circle"></i> Thêm Mã giảm giá mới</a>
    </div>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Mã Code</th>
                    <th>Tên</th>
                    <th>Loại giảm giá</th>
                    <th>Giá trị</th>
                    <th>Đơn tối thiểu</th>
                    <th>Giảm tối đa</th>
                    <th>Giới hạn sử dụng</th>
                    <th>Đã sử dụng</th>
                    <th>Hoạt động</th>
                    <th>Ngày bắt đầu</th>
                    <th>Ngày kết thúc</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="coupon" items="${requestScope.couponList}">
                    <tr>
                        <td>${coupon.couponID}</td>
                        <td>${coupon.couponCode}</td>
                        <td>${coupon.couponName}</td>
                        <td>${coupon.discountType}</td>
                        <td>
                            <c:choose>
                                <c:when test="${coupon.discountType == 'percentage'}">
                                    <fmt:formatNumber value="${coupon.discountValue}" type="percent"/>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${coupon.discountValue}" type="currency" currencySymbol="₫"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatNumber value="${coupon.minOrderAmount}" type="currency" currencySymbol="₫"/></td>
                        <td><fmt:formatNumber value="${coupon.maxDiscountAmount}" type="currency" currencySymbol="₫"/></td>
                        <td>${coupon.usageLimit != null ? coupon.usageLimit : 'Không giới hạn'}</td>
                        <td>${coupon.usedCount}</td>
                        <td>
                            <c:choose>
                                <c:when test="${coupon.active}"><span class="badge bg-success">Có</span></c:when>
                                <c:otherwise><span class="badge bg-danger">Không</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td><fmt:formatDate value="${coupon.startDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td><fmt:formatDate value="${coupon.endDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/coupons/edit?id=${coupon.couponID}" class="btn btn-sm btn-warning me-1"><i class="fas fa-edit"></i> Sửa</a>
                            <a href="${pageContext.request.contextPath}/admin/coupons/delete?id=${coupon.couponID}" class="btn btn-sm btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa mã giảm giá này không?');"><i class="fas fa-trash"></i> Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty requestScope.couponList}">
                    <tr>
                        <td colspan="13" class="text-center text-muted">Không có mã giảm giá nào để hiển thị.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />