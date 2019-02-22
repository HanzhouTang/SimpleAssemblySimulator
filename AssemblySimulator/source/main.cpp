#include"LRUCache.h"
#include<iostream>
#include<string>
int main() {
	using namespace std;
	LRUCache<int, string> m(2);
	m.put(2, "two");
	m.put(3, "three");
	m.put(5, "six");
	cout << m.get(2) << endl;
	cout << m.get(5) << endl;
	system("pause");
}