<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4"><c:choose><c:when test="${not empty requestScope.coupon}">Sửa Mã giảm giá</c:when><c:otherwise>Thêm Mã giảm giá mới</c:otherwise></c:choose></h1>

    <c:if test="${not empty requestScope.errorMessage}">
        <div class="alert alert-danger text-center" role="alert">
            ${requestScope.errorMessage}
        </div>
    </c:if>

    <div class="card p-4 shadow-sm">
        <form action="${pageContext.request.contextPath}/admin/coupons/<c:choose><c:when test="${not empty requestScope.coupon}">edit</c:when><c:otherwise>add</c:otherwise></c:choose>" method="post">
            <c:if test="${not empty requestScope.coupon}">
                <input type="hidden" name="couponId" value="${requestScope.coupon.couponID}">
            </c:if>

            <div class="mb-3">
                <label for="couponCode" class="form-label">Mã Code:</label>
                <input type="text" class="form-control" id="couponCode" name="couponCode" value="${requestScope.coupon.couponCode}" required>
            </div>
            <div class="mb-3">
                <label for="couponName" class="form-label">Tên Mã giảm giá:</label>
                <input type="text" class="form-control" id="couponName" name="couponName" value="${requestScope.coupon.couponName}">
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Mô tả:</label>
                <textarea class="form-control" id="description" name="description" rows="3">${requestScope.coupon.description}</textarea>
            </div>
            <div class="mb-3">
                <label for="discountType" class="form-label">Loại giảm giá:</label>
                <select class="form-select" id="discountType" name="discountType" required>
                    <option value="percentage" <c:if test="${requestScope.coupon.discountType == 'percentage'}">selected</c:if>>Phần trăm</option>
                    <option value="fixed_amount" <c:if test="${requestScope.coupon.discountType == 'fixed_amount'}">selected</c:if>>Số tiền cố định</option>
                </select>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="discountValue" class="form-label">Giá trị giảm giá:</label>
                    <input type="number" class="form-control" id="discountValue" name="discountValue" step="0.01" value="${requestScope.coupon.discountValue}" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="minOrderAmount" class="form-label">Đơn hàng tối thiểu:</label>
                    <input type="number" class="form-control" id="minOrderAmount" name="minOrderAmount" step="0.01" value="${requestScope.coupon.minOrderAmount}">
                </div>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="maxDiscountAmount" class="form-label">Giảm giá tối đa:</label>
                    <input type="number" class="form-control" id="maxDiscountAmount" name="maxDiscountAmount" step="0.01" value="${requestScope.coupon.maxDiscountAmount}">
                </div>
                <div class="col-md-6 mb-3">
                    <label for="usageLimit" class="form-label">Giới hạn sử dụng (tổng số lần):</label>
                    <input type="number" class="form-control" id="usageLimit" name="usageLimit" value="${requestScope.coupon.usageLimit}">
                </div>
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" type="checkbox" id="isActive" name="isActive" <c:if test="${requestScope.coupon.active}">checked</c:if>>
                <label class="form-check-label" for="isActive">Hoạt động</label>
            </div>
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="startDate" class="form-label">Ngày bắt đầu:</label>
                    <input type="datetime-local" class="form-control" id="startDate" name="startDate" value="<fmt:formatDate value='${requestScope.coupon.startDateAsDate}' pattern="yyyy-MM-dd'T'HH:mm"/>" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="endDate" class="form-label">Ngày kết thúc:</label>
                    <input type="datetime-local" class="form-control" id="endDate" name="endDate" value="<fmt:formatDate value='${requestScope.coupon.endDateAsDate}' pattern="yyyy-MM-dd'T'HH:mm"/>" required>
                </div>
            </div>

            <button type="submit" class="btn btn-primary w-100"><c:choose><c:when test="${not empty requestScope.coupon}">Cập nhật Mã giảm giá</c:when><c:otherwise>Thêm Mã giảm giá</c:otherwise></c:choose></button>
        </form>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/admin/coupons" class="btn btn-secondary">Quay lại danh sách mã giảm giá</a>
        </div>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />