// Oliver Tonnesen
// V00885732

#include <iostream>
#include <bits/stdc++.h>
using namespace std;

int main(void) {
	int x [3];
	cout << "Enter a number:  ";
	cin >> x[0];
	cout << "Enter a number: ";
	cin >> x[1];
	cout << "Enter a number: ";
	cin >> x[2];
	sort(x, x+3);
	for (int i = 0; i < sizeof(x)/sizeof(x[0]); i++)
		cout << x[i] << '\n';
	return 0;
}
