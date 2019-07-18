/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

// event representation
class Event implements Comparable {

 public Event(int a_type, double a_time) { _type = a_type; time = a_time; }
  
 public double time;
 private final int _type;
 
 public int get_type() { return _type; }
 public double get_time() { return time; }

 public Event leftlink, rightlink, uplink;

 @Override
 public int compareTo(Object _cmpEvent ) {
  double _cmp_time = ((Event) _cmpEvent).get_time() ;
  if( this.time < _cmp_time) return -1;
  if( this.time == _cmp_time) return 0;
  return 1;
 }
};
