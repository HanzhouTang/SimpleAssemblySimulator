#include<iostream>
extern "C" int add_(int a, int b, int c);
int main() {
	using namespace std;
	cout << add_(10, 11, 12) << endl;
	system("pause");
	return 0;
}