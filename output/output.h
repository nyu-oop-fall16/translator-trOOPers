#include "java_lang.h"

using namespace java::lang;

namespace inputs {
namespace test027 {
struct __A;
struct __A_VT;
typedef __A* A;

struct __A {
 __A_VT* __vptr ;

static __A_VT __vtable ;

 int i ;

__A();

__A(int i);

static void __init(A);
static void __init(A, int);
static int get(A);
static Class __class();
};

struct __A_VT {
Class isa;
int (*hashCode)(A);
bool (*equals)(A, Object);
Class (*getClass)(A);
String (*toString)(A);
int (*get)(A);

__A_VT() 
:
isa(__A::__class()),
hashCode((int(*)(A)) &__Object::hashCode),
equals((bool(*)(A, Object)) &__Object::equals),
getClass((Class(*)(A)) &__Object::getClass),
toString((String(*)(A)) &__Object::toString),
get(&__A::get) {
}
};

}
}
