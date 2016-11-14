#include "output.h"
 #include "java_lang.h"
 using namespace java::lang;
 namespace inputs {
 namespace test006 {
 String __A::__A ( ) {
 } 
 __A::setFld ( String f ) {
 : fld=f , __vptr(&__vtable) } 
 __A::almostSetFld ( String f ) {
 String : fld=f , __vptr(&__vtable) } 
 String __A::getFld ( A __this ) {
 return __this->fld ;
 } 
 Class __A::__class ( ) {
 static Class k = new __Class(__rt::literal("java.lang.A"), (Class)__Object::__class()); 
 return k ;
 } 
 __A_VT __A:: __vtable; 
 }
 }
