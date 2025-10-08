<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="admin-header.jsp" />

<div class="container-fluid">
    <h1 class="mb-4">Chào mừng đến với Bảng điều khiển Admin</h1>

    <div class="row dashboard-widgets g-4">
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Sản phẩm</h3>
                    <p class="card-text display-4 text-primary">${requestScope.totalProducts}</p>
                    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-primary mt-3">Xem Sản phẩm</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Đơn hàng</h3>
                    <p class="card-text display-4 text-info">${requestScope.totalOrders}</p>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-info text-white mt-3">Xem Đơn hàng</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Người dùng</h3>
                    <p class="card-text display-4 text-success">${requestScope.totalUsers}</p>
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-success mt-3">Xem Người dùng</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Danh mục</h3>
                    <p class="card-text display-4 text-warning">${requestScope.totalCategories}</p>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-warning text-white mt-3">Xem Danh mục</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Thương hiệu</h3>
                    <p class="card-text display-4 text-danger">${requestScope.totalBrands}</p>
                    <a href="${pageContext.request.contextPath}/admin/brands" class="btn btn-danger mt-3">Xem Thương hiệu</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Đánh giá</h3>
                    <p class="card-text display-4 text-secondary">${requestScope.totalReviews}</p>
                    <a href="${pageContext.request.contextPath}/admin/reviews" class="btn btn-secondary mt-3">Xem Đánh giá</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Mã giảm giá</h3>
                    <p class="card-text display-4 text-dark">${requestScope.totalCoupons}</p>
                    <a href="${pageContext.request.contextPath}/admin/coupons" class="btn btn-dark mt-3">Xem Mã giảm giá</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Giao dịch Tồn kho</h3>
                    <p class="card-text display-4 text-primary">${requestScope.totalInventoryTransactions}</p>
                    <a href="${pageContext.request.contextPath}/admin/inventory" class="btn btn-primary mt-3">Xem Giao dịch Tồn kho</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Đơn hàng Chờ xử lý</h3>
                    <p class="card-text display-4 text-warning">${requestScope.totalPendingOrders}</p>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=Pending" class="btn btn-warning text-white mt-3">Xem Đơn hàng Chờ xử lý</a>
                </div>
            </div>
        </div>
        <div class="col-md-4 col-lg-3">
            <div class="card widget shadow-sm h-100">
                <div class="card-body">
                    <h3 class="card-title">Tổng số Đơn hàng Đang xử lý</h3>
                    <p class="card-text display-4 text-info">${requestScope.totalProcessingOrders}</p>
                    <a href="${pageContext.request.contextPath}/admin/orders?status=Processing" class="btn btn-info text-white mt-3">Xem Đơn hàng Đang xử lý</a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="admin-footer.jsp" />