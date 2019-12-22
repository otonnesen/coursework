function root = Bisect (xl, xu, eps, imax, f, enablePlot)
x = [xl:0.01:xu];
if enablePlot
    hold on;
    y0 = yline(0, '--k', 'x = 0');
    y0.LabelHorizontalAlignment = 'left';
end
i = 1;
fl = f(xl);
fprintf ('iteration\tapproximation\n')
while i <= imax
    xr = (xl + xu)/2;
    fprintf ( '%5.0f %17.7f\n', i, xr )
    fr = f(xr);
    if enablePlot
        if ismember(i, [1,3,5,6])
            z = [xl, xr, xu];
            fz = f(z);
            xlabel('y');
            ylabel('x');
            plot(x, f(x), '-k', z, fz, '*b');
        end
    end
    if fr == 0 || (xu-xl)/abs(xu+xl) < eps
        root = xr;
        if enablePlot
            hold off;
        end
        return;
    end
    i = i + 1;
    if fl * fr < 0
        xu = xr;
    else
        xl = xr;
        fl = fr;
    end
end
if enablePlot
    hold off;
end
fprintf('Failed to converge in %g iterations.\n', imax);
root = NaN;