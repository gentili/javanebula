/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ca.mcpnet.demurrage.GameClient.jme;

import ca.mcpnet.demurrage.GameClient.jme.IntMap.Entry;

import java.util.Iterator;

/**
 * Taken from http://code.google.com/p/skorpios/
 * 
 * @author Nate 
 */
public final class IntMap<T> implements Iterable<Entry<T>>, Cloneable {

    private Entry<T>[] table;
    private final float loadFactor;
    private int size, mask, capacity, threshold;

    public IntMap() {
        this(16, 0.75f);
    }

    public IntMap(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    @SuppressWarnings("unchecked")
	public IntMap(int initialCapacity, float loadFactor) {
        if (initialCapacity > 1 << 30){
            throw new IllegalArgumentException("initialCapacity is too large.");
        }
        if (initialCapacity < 0){
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        }
        if (loadFactor <= 0){
            throw new IllegalArgumentException("initialCapacity must be greater than zero.");
        }
        capacity = 1;
        while (capacity < initialCapacity){
            capacity <<= 1;
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.table = new Entry[capacity];
        this.mask = capacity - 1;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public IntMap<T> clone(){
        try{
            IntMap<T> clone = (IntMap<T>) super.clone();
            Entry[] newTable = new Entry[table.length];
            for (int i = table.length - 1; i >= 0; i--){
                if (table[i] != null)
                    newTable[i] = table[i].clone();
            }
            clone.table = newTable;
            return clone;
        }catch (CloneNotSupportedException ex){
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean containsValue(Object value) {
        Entry[] table = this.table;
        for (int i = table.length; i-- > 0;){
            for (Entry<T> e = table[i]; e != null; e = e.next){
                if (e.value.equals(value)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsKey(int key) {
        int index = ((int) key) & mask;
        for (Entry<T> e = table[index]; e != null; e = e.next){
            if (e.key == key){
                return true;
            }
        }
        return false;
    }

    public T get(int key) {
        int index = key & mask;
        for (Entry<T> e = table[index]; e != null; e = e.next){
            if (e.key == key){
                return e.value;
            }
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public T put(int key, T value) {
        int index = key & mask;
        // Check if key already exists.
        for (Entry<T> e = table[index]; e != null; e = e.next){
            if (e.key != key){
                continue;
            }
            Object oldValue = e.value;
            e.value = value;
            return (T) oldValue;
        }
        table[index] = new Entry<T>(key, value, table[index]);
        if (size++ >= threshold){
            // Rehash.
            int newCapacity = 2 * capacity;
            Entry[] newTable = new Entry[newCapacity];
            Entry[] src = table;
            int bucketmask = newCapacity - 1;
            for (int j = 0; j < src.length; j++){
                Entry<T> e = src[j];
                if (e != null){
                    src[j] = null;
                    do{
                        Entry<T> next = e.next;
                        index = e.key & bucketmask;
                        e.next = newTable[index];
                        newTable[index] = e;
                        e = next;
                    }while (e != null);
                }
            }
            table = newTable;
            capacity = newCapacity;
            threshold = (int) (newCapacity * loadFactor);
            mask = capacity - 1;
        }
        return null;
    }

    public T remove(int key) {
        int index = key & mask;
        Entry<T> prev = table[index];
        Entry<T> e = prev;
        while (e != null){
            Entry<T> next = e.next;
            if (e.key == key){
                size--;
                if (prev == e){
                    table[index] = next;
                }else{
                    prev.next = next;
                }
                return e.value;
            }
            prev = e;
            e = next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("rawtypes")
	public void clear() {
        Entry[] table = this.table;
        for (int index = table.length; --index >= 0;){
            table[index] = null;
        }
        size = 0;
    }

    public Iterator<Entry<T>> iterator() {
        return (Iterator<Entry<T>>) new IntMapIterator();
    }

    final class IntMapIterator implements Iterator<Entry<T>> {

        /**
         * Current entry.
         */
        private Entry<T> cur;

        /**
         * Entry in the table
         */
        private int idx = 0;

        /**
         * Element in the entry
         */
        private int el  = 0;

        public IntMapIterator() {
            cur = table[0];
        }

        public boolean hasNext() {
            return el < size;
        }

        public Entry<T> next() {
            if (el >= size)
                throw new IllegalStateException("No more elements!");

            if (cur != null){
                Entry<T> e = cur;
                cur = cur.next;
                el++;
                return e;
            }
//            if (cur != null && cur.next != null){
                // if we have a current entry, continue to the next entry in the list
//                cur = cur.next;
//                el++;
//                return cur;
//            }

            do {
                // either we exhausted the current entry list, or
                // the entry was null. find another non-null entry.
                cur = table[++idx];
            } while (cur == null);
            Entry<T> e = cur;
            cur = cur.next;
            el ++;
            return e;
        }

        public void remove() {
        }
        
    }
    
    public static final class Entry<T> implements Cloneable {

        final int key;
        T value;
        Entry<T> next;

        Entry(int k, T v, Entry<T> n) {
            key = k;
            value = v;
            next = n;
        }

        public int getKey(){
            return key;
        }

        public T getValue(){
            return value;
        }

        public String toString(){
            return key + " => " + value;
        }

        @SuppressWarnings("unchecked")
		public Entry<T> clone(){
            try{
                Entry<T> clone = (Entry<T>) super.clone();
                clone.next = next != null ? next.clone() : null;
                return clone;
            }catch (CloneNotSupportedException ex){
            }
            return null;
        }
    }
}
