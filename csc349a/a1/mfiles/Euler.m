function Euler(m,c,g,t0,v0,tn,n)
fprintf('values of t\tapproximations v(t)\n')
fprintf('%8.3f%19.4f\n', t0, v0)
h=(tn-t0)/n;
t=t0;
v=v0;
for i=1:n
    v=v+(g-c/m*v)*h;
    t=t+h;
    fprintf('%8.3f%19.4f\n', t, v)
end