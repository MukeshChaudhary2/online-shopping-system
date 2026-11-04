        </div> <!-- /.container -->
    </main>

    <!-- Footer -->
    <footer class="site-footer">
        <div class="container">
            <div class="footer-grid">
                <div class="footer-col">
                    <h4 style="color: white; font-weight: 700;">ShopHub Online System</h4>
                    <p style="font-size: 0.9rem; line-height: 1.6; margin-bottom: 1rem;">
                        A full-stack, enterprise-grade e-commerce web platform developed for IGNOU BCA Final Year Project (BCSP-064).
                    </p>
                    <p style="font-size: 0.8rem; color: #64748b;">
                        Tech: Java 17 &bull; Jakarta Servlets &bull; JSP/JSTL &bull; MySQL 8 &bull; HikariCP
                    </p>
                </div>

                <div class="footer-col">
                    <h4>Customer Care</h4>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/contact">Contact Us & Feedback</a></li>
                        <li><a href="${pageContext.request.contextPath}/products">Browse Catalog</a></li>
                        <li><a href="${pageContext.request.contextPath}/my-orders">Track Orders</a></li>
                    </ul>
                </div>

                <div class="footer-col">
                    <h4>User Account</h4>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/login">Customer Login</a></li>
                        <li><a href="${pageContext.request.contextPath}/register">Create New Account</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/login">Admin Management</a></li>
                    </ul>
                </div>

                <div class="footer-col">
                    <h4>Security Architecture</h4>
                    <p style="font-size: 0.85rem; line-height: 1.5; margin-bottom: 0.5rem;">
                        &check; BCrypt Salted Password Hashing<br>
                        &check; PreparedStatement SQL Injection Block<br>
                        &check; Cross-Site Request Forgery (CSRF) Guard<br>
                        &check; ACID Transactional Checkout
                    </p>
                </div>
            </div>

            <div class="footer-bottom">
                &copy; <%= java.time.Year.now().getValue() %> Online Shopping System (BCSP-064). All Rights Reserved.
            </div>
        </div>
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
    <script>
        // Check live cart badge count on page load
        document.addEventListener('DOMContentLoaded', function() {
            fetch('${pageContext.request.contextPath}/cart/count')
                .then(res => res.json())
                .then(data => {
                    const badge = document.getElementById('nav-cart-badge');
                    if (badge && data && data.count !== undefined) {
                        badge.textContent = data.count;
                        badge.style.display = data.count > 0 ? 'inline-block' : 'none';
                    }
                })
                .catch(() => {});
        });
    </script>
</body>
</html>
