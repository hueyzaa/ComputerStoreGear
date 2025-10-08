<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f8f9fa;
            display: flex;
            min-height: 100vh;
        }
        .admin-sidebar {
            width: 250px;
            background-color: #212529;
            color: white;
            padding: 20px;
            flex-shrink: 0;
        }
        .admin-sidebar .nav-link {
            color: white;
            padding: 10px 15px;
            margin-bottom: 5px;
            border-radius: 5px;
        }
        .admin-sidebar .nav-link:hover,
        .admin-sidebar .nav-link.active {
            background-color: #007bff;
            color: white;
        }
        .admin-main-content {
            flex-grow: 1;
            padding: 20px;
            background-color: #f8f9fa;
        }
        .admin-navbar {
            background-color: #ffffff;
            border-bottom: 1px solid #e9ecef;
            margin-bottom: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        .admin-navbar .navbar-brand {
            color: #212529;
            font-weight: bold;
        }
        .admin-navbar .nav-link {
            color: #212529;
        }
        .admin-navbar .nav-link:hover {
            color: #007bff;
        }
    </style>
</head>
<body>
    <div class="admin-sidebar d-flex flex-column p-3">
        <h2 class="text-center mb-4">Admin Panel</h2>
        <nav class="nav flex-column">
            <a class="nav-link active" href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt me-2"></i>Dashboard</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/products"><i class="fas fa-box me-2"></i>Quản lý Sản phẩm</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/orders"><i class="fas fa-shopping-cart me-2"></i>Quản lý Đơn hàng</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/categories"><i class="fas fa-tags me-2"></i>Quản lý Danh mục</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/brands"><i class="fas fa-copyright me-2"></i>Quản lý Thương hiệu</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/users"><i class="fas fa-users me-2"></i>Quản lý Người dùng</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reviews"><i class="fas fa-comments me-2"></i>Quản lý Đánh giá</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/coupons"><i class="fas fa-gift me-2"></i>Quản lý Mã giảm giá</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/inventory"><i class="fas fa-warehouse me-2"></i>Quản lý Tồn kho</a>
            <hr class="text-white-50">
            <a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt me-2"></i>Đăng Xuất</a>
        </nav>
    </div>

    <div class="admin-main-content">
        <nav class="admin-navbar navbar navbar-expand-lg">
            <div class="container-fluid">
                <a class="navbar-brand" href="#">Bảng điều khiển Admin</a>
                <div class="collapse navbar-collapse justify-content-end">
                    <ul class="navbar-nav">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/home"><i class="fas fa-home me-1"></i>Trang chủ</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/profile"><i class="fas fa-user me-1"></i>Hồ sơ</a>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
