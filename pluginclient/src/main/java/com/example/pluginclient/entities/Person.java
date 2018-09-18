package com.example.pluginclient.entities;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable
{

    private String name = "";

    private int age = 0;

    private ArrayList<String> books = new ArrayList<String>();
    
    /**
     *
     */
    private TestItem item = null;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public ArrayList<String> getBooks()
    {
        return books;
    }

    public TestItem getItem()
    {
        return item;
    }

    public void setItem(TestItem item)
    {
        this.item = item;
    }

    public void setBooks(ArrayList<String> books)
    {
        this.books = books;
    }


    @Override
    public String toString()
    {
        return "Person [name=" + name + ", age=" + age + ", books=" + books + ", item=" + item + "]";
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeStringList(books);
        dest.writeSerializable(item);
    }

    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>()
    {
        // 重写Creator
        @Override
        public Person createFromParcel(Parcel source)
        {
            Person p = new Person();
            p.name = source.readString();
            p.age = source.readInt();
            source.readStringList(p.books);
            p.item =  (TestItem) source.readSerializable();
            return p;
        }

        @Override
        public Person[] newArray(int size)
        {
            // TODO Auto-generated method stub
            return null;
        }
    };
}
