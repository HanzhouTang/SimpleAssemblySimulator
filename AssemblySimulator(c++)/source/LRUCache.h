#pragma once
#include<unordered_map>
#include<map>
#include"Cache.h"
template<typename K, typename V>
class LRUCache : Cache<K,V>  {
private:
	size_t capacity;
	using StampType = unsigned long long;
	StampType count = 0;
	std::unordered_map<K,StampType> key2stamp;
	std::map<StampType, K> stamp2key;
	std::unordered_map<K,V> data;
public:
	LRUCache(std::size_t capacity) : capacity(capacity) {

	}

	virtual V get(const K& key) override{
		if (data.count(key)) {
			count++;
			auto stamp = key2stamp[key];
			stamp2key.erase(stamp);
			key2stamp[key] = count;
			stamp2key[count] = key;
			return data[key];
		}
		return V();
	}

	virtual void put(const K& key, const V& value) override {
		count++;
		if (data.count(key)) {
			auto stamp = key2stamp[key];
			stamp2key.erase(stamp);
			data[key] = value;
		}
		else if (data.size()<capacity) {
			data[key] = value;
		}
		else {
			auto ptr = stamp2key.begin();
			auto least = ptr->second;
			data.erase(least);
			data[key] = value;
			stamp2key.erase(ptr);
			key2stamp.erase(least);
		}
		stamp2key[count] = key;
		key2stamp[key] = count;
	}
};
