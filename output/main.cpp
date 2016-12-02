#include <iostream>
#include "output.h"
#include "java_lang.h"

using namespace std;
using namespace java::lang;
using namespace inputs::test007;

int main()
{
    B b = new __B();
    std::cout << b->a << std::endl;
    std::cout << b->b << std::endl;
    return 0;
}