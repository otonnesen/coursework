function exp_inv_2(x,n)
s=0;
fprintf("n\tapproximation e^x\trelative error\n");
for i=0:n
    s=s+(x^i)/factorial(i);
    fprintf("%d\t\t%.4f\t\t\t%.4f\n", i, 1/s, abs(1-(1/s)/exp(-1*x)));
end