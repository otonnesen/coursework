function trap(a, b, maxiter, tol, f)
m = 1;
x = linspace(a, b, m+1);
y = f(x);
approx = trapz(x, y);
fprintf('   \tm  \tintegral approximation\n');
fprintf(' %5.0f %16.10f\n', m, approx);
for i = 1 : maxiter
    m = m * 2;
    oldapprox = approx;
    x = linspace(a, b, m+1);
    y = f(x);
    approx = trapz(x, y);
    fprintf(' %5.0f %16.10f\n', m, approx);
    
    if abs(1-oldapprox/approx) < tol
        return
    end
end
fprintf('Did not converge in %g iterations\n', maxiter);