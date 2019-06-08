def T(n):
    if (n==1):
        return 2
    return 3*T(n/7)+(n/7)+1

def S(n):
    t = 2*(3**n)
    for i in xrange(n):
        t+=(3**i)*((7**(n-1-i))+1)
    return t

for i in xrange(100):
    print("n = "+str(7**i)+": "+str(T(7**i)==S(i)))
