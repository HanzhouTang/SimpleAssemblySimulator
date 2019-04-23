#include<iostream>
extern "C" int add_(int a, int b, int c);
extern "C" int divid_(int a, int b, int* quo, int* rem);
extern "C" const int NumFibVals_;
extern "C" const int* FibMem_;

int main() {
	using namespace std;
	cout<<"add " << add_(10, 11, 12) << endl;
	int quo = 0;
	int rem = 0;
	if (divid_(33, 0, &quo, &rem)) {
		cout << "divid quo " << quo << " rem " << rem << endl;
	}
	else {
		cout << "divided by 0"<<endl;
	}
	cout << "NumFibVals " << NumFibVals_ << endl;
	for (int i = 0; i < NumFibVals_; i++) {
		cout << FibMem_[i] << " ";
	}
	cout << endl;

	
	cout << endl;
	system("pause");
	return 0;
}