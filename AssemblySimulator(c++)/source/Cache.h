#pragma once
template<typename K, typename V>
class Cache {
	virtual V get(const K& key) { return V(); }
	virtual void put(const K& key, const V& value){}
};