function exp_inv_1(x,n)
s=0;
sgn=1;
fprintf("n\tapproximation e^-x\trelative error\n");
for i=0:n
    s=s+sgn*(x^i)/factorial(i);
    sgn = sgn*-1;
    fprintf("%d\t\t%.4f\t\t\t%.4f\n", i, s, abs(1-s/exp(-1*x)));
end