package inputs.test018;

public class Test018 {
    static int x;

    public static void main(String[] args) {
        {
            int x;
            x = 3;
        }
        System.out.println(x);
    }
}


/*
#include "output.h"
#include<iostream>

using namespace inputs::test018
using namespace std

int x;

int main(){
{
int x;
x=3;
}
std::cout << x << std::endl
}

 */
