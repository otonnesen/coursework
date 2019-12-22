function SLEDiag(A, b)
x = zeros(1, size(A, 1));
x(1) = b(1)/A(1,1);
x(2) = (b(2)-A(2,1)*x(1))/A(2,2);

for i = 3:size(A,1)
    x(i) = (b(i)-A(i,i-2)*x(i-2)-A(i,i-1)*x(i-1))/A(i,i);
end

disp(x);