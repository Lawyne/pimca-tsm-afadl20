package plug.language.tsm.examples.cta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;
import plug.language.tsm.ast.Channel;

public class ExMain {
	

	List<Behavior<Configuration>> waterTank() {

        Behavior<Configuration> w2s =
                new Behavior<>(
                         "w2s",
                        (c) -> c.wtTriggerSensor,
                        (c) -> {
                        	c.wtTriggerSensor = false;
                        	return c;
                        }, 
                        Channel.out("measure")
                        ,true);

        Behavior<Configuration> r2i =
                new Behavior<>(
                         "r2i",
                        (c) -> c.wtWaterLevel < c.wtMaxWaterLevel,
                        (c) -> {
                        	c.wtWaterLevel+=1;
                        	c.wtTriggerSensor = true;
                        	return c;
                        }, 
                        Channel.in("increase")
                        ,false);

        Behavior<Configuration> i2f =
                new Behavior<>(
                         "i2f",
                        (c) -> c.wtWaterLevel == c.wtMaxWaterLevel,
                        (c) -> {
                        	c.wtOverflow = true;
                        	c.wtTriggerSensor = true;
                        	return c;
                        }, 
                        Channel.in("increase")
                        ,false);

        Behavior<Configuration> r2d =
                new Behavior<>(
                         "r2d",
                        (c) -> c.wtWaterLevel > 0,
                        (c) -> {
                        	c.wtWaterLevel-=1;
                        	c.wtTriggerSensor = true;
                        	return c;
                        }, 
                        Channel.in("decrease")
                        ,false);

        Behavior<Configuration> d2e =
                new Behavior<>(
                         "d2e",
                        (c) -> c.wtWaterLevel == 0,
                        (c) -> {
                        	c.wtTriggerSensor=true;
                        	return c;
                        }, 
                        Channel.in("decrease")
                        ,false);
        return Arrays.asList(w2s,r2i,i2f,r2d,d2e);
	}
	
	List<Behavior<Configuration>> plc() {

        Behavior<Configuration> upd =
                new Behavior<>(
                         "upd",
                        (c) -> true,
                        (c) -> {
                        	c.plcWaterLevel=c.sWaterLevel;
                        	c.plcTriggerDecision=true;
                        	return c;
                        }, 
                        Channel.in("updateLevel")
                        ,false);

        Behavior<Configuration> t2r =
                new Behavior<>(
                         "t2r",
                        (c) -> c.plcTriggerDecision
                        	&& c.plcWaterLevel<c.plcUpperThreshold
                        	&& c.plcWaterLevel>c.plcLowerThreshold,
                        (c) -> {
                        	c.plcTriggerDecision = false;
                        	c.plcTriggerRegular = true;
                        	return c;
                        }
                        ,true);

        Behavior<Configuration> t2u =
                new Behavior<>(
                         "t2u",
                        (c) -> c.plcTriggerDecision
                    		&& c.plcWaterLevel>=c.plcUpperThreshold,
                        (c) -> {
                        	c.plcTriggerDecision = false;
                        	c.plcTriggerDangerous = true;
                        	c.plcTriggerIVOff = true;
                        	c.plcTriggerPumpOn = true;
                        	return c;
                        }
                        ,true);

        Behavior<Configuration> t2d =
                new Behavior<>(
                         "t2d",
                        (c) -> c.plcTriggerDecision
                			&& c.plcWaterLevel<=c.plcLowerThreshold,
                        (c) -> {
                        	c.plcTriggerDecision = false;
                        	c.plcTriggerDangerous = true;
                        	c.plcTriggerIVOn = true;
                        	c.plcTriggerPumpOff = true;
                        	return c;
                        }
                        ,true);

        Behavior<Configuration> p2io =
                new Behavior<>(
                         "p2io",
                        (c) -> c.plcTriggerIVOn,
                        (c) -> {
                        	c.plcTriggerIVOn = false;
                        	return c;
                        }, 
                        Channel.out("commandIVOn")
                        ,true);

        Behavior<Configuration> p2po =
                new Behavior<>(
                         "p2po",
                        (c) -> c.plcTriggerPumpOn,
                        (c) -> {
                        	c.plcTriggerPumpOn = false;
                        	return c;
                        }, 
                        Channel.out("commandPumpOn")
                        ,true);
        Behavior<Configuration> p2ic =
                new Behavior<>(
                         "p2ic",
                        (c) -> c.plcTriggerIVOff,
                        (c) -> {
                        	c.plcTriggerIVOff = false;
                        	return c;
                        }, 
                        Channel.out("commandIVOff")
                        ,true);

        Behavior<Configuration> p2pc =
                new Behavior<>(
                         "p2pc",
                        (c) -> c.plcTriggerPumpOff,
                        (c) -> {
                        	c.plcTriggerPumpOff = false;
                        	return c;
                        }, 
                        Channel.out("commandPumpOff")
                        ,true);

        Behavior<Configuration> p2d =
                new Behavior<>(
                         "p2d",
                        (c) -> c.plcTriggerDangerous,
                        (c) -> {
                        	c.plcTriggerDangerous = false;
                        	return c;
                        }, 
                        Channel.out("dangerousLevel")
                        ,true);

        Behavior<Configuration> p2r =
                new Behavior<>(
                         "p2r",
                        (c) -> c.plcTriggerRegular,
                        (c) -> {
                        	c.plcTriggerRegular = false;
                        	return c;
                        }, 
                        Channel.out("regularLevel")
                        ,true);
        return Arrays.asList(upd,t2r,t2u,t2d,p2io,p2po,p2ic,p2pc,p2d,p2r);
	}
	
	List<Behavior<Configuration>> scada() {

        Behavior<Configuration> e2r =
                new Behavior<>(
                         "e2r",
                        (c) -> c.sEmergency,
                        (c) -> {
                        	c.sEmergency=false;
                        	return c;
                        }, 
                        Channel.in("regularLevel")
                        ,false);

        Behavior<Configuration> r2r =
                new Behavior<>(
                        "r2r",
                       (c) -> !c.sEmergency,
                       (c) -> {
                       	return c;
                       }, 
                       Channel.in("regularLevel")
                       ,false);

        Behavior<Configuration> a2c =
                new Behavior<>(
                         "a2c",
                        (c) -> true,
                        (c) -> {
                        	c.sCorrupted = true;
                        	return c;
                        }, 
                        Channel.in("jamNetwork")
                        ,false);

        Behavior<Configuration> r2e =
                new Behavior<>(
                         "r2e",
                        (c) -> !c.sEmergency&&!c.sAlert&&!c.sCorrupted,
                        (c) -> {
                        	c.sEmergency=true;
                        	return c;
                        }, 
                        Channel.in("dangerousLevel")
                        ,false);

        Behavior<Configuration> e2a =
                new Behavior<>(
                         "e2a",
                        (c) -> c.sEmergency&&!c.sAlert&&!c.sCorrupted,
                        (c) -> {
                        	c.sAlert=true;
                        	return c;
                        }, 
                        Channel.in("dangerousLevel")
                        ,false);

        Behavior<Configuration> a2a =
                new Behavior<>(
                         "a2a",
                        (c) -> c.sAlert||c.sCorrupted,
                        (c) -> {
                        	return c;
                        }, 
                        Channel.in("dangerousLevel")
                        ,false);
        return Arrays.asList(e2r,r2r,r2e,e2a,a2c,a2a);
	}
	
	List<Behavior<Configuration>> inflowValve() {

        Behavior<Configuration> o2crit =
                new Behavior<>(
                         "IV.o2crit",
                        (c) -> c.iIsOpen && !c.flagIV,
                        (c) -> {
                        	c.flagIV=true;
                        	return c;
                        }
                        ,true);		
		
        Behavior<Configuration> o2o =
                new Behavior<>(
                         "IV.o2o",
                        (c) -> c.iIsOpen && (!c.flagPump || c.turn==c.TURN_IV),
                        (c) -> {
                        	c.flagIV=false;
                        	c.turn=c.TURN_PUMP;
                        	return c;
                        }, 
                        Channel.out("increase")
                        ,false);
        
        Behavior<Configuration> f2fo =
                new Behavior<>(
                         "IV.f2fo",
                        (c) -> c.iIsForced,
                        (c) -> {
                        	return c;
                        }, 
                        Channel.in("commandIVOn")
                        ,false);
        
        Behavior<Configuration> f2fc =
                new Behavior<>(
                         "IV.f2fc",
                        (c) -> c.iIsForced,
                        (c) -> {
                        	return c;
                        }, 
                        Channel.in("commandIVOff")
                        ,false);
        
        Behavior<Configuration> a2f =
                new Behavior<>(
                         "IV.a2f",
                        (c) -> true,
                        (c) -> {
                        	c.iIsForced = true;
                        	c.iIsOpen = true;
                        	return c;
                        }, 
                        Channel.in("forceOpen")
                        ,false);
        
        Behavior<Configuration> c2o =
                new Behavior<>(
                         "IV.c2o",
                        (c) -> !c.iIsForced,
                        (c) -> {
                        	c.iIsOpen=true;
                        	return c;
                        }, 
                        Channel.in("commandIVOn")
                        ,false);
        
        Behavior<Configuration> o2c =
                new Behavior<>(
                         "IV.o2c",
                        (c) -> !c.iIsForced,
                        (c) -> {
                        	c.iIsOpen=false;
                        	c.flagIV=false;
                        	return c;
                        }, 
                        Channel.in("commandIVOff")
                        ,false);
        return Arrays.asList(o2crit,o2o,f2fo,f2fc,a2f,o2c,c2o);
	}

	List<Behavior<Configuration>> attacker() {

        Behavior<Configuration> z2o =
                new Behavior<>(
                         "z2o",
                        (c) -> !c.aHasJammedNetwork,
                        (c) -> {
                        	c.aHasJammedNetwork = true;
                        	return c;
                        }, 
                        Channel.out("jamNetwork")
                        ,false);
        
        Behavior<Configuration> o2t =
                new Behavior<>(
                         "o2t",
                        (c) -> !c.aHasForced,
                        (c) -> {
                        	c.aHasForced = true;
                        	return c;
                        }, 
                        Channel.out("forceOpen")
                        ,false);
        
        Behavior<Configuration> t2t =
                new Behavior<>(
                         "t2t",
                        (c) -> !c.aHasManuallyInput,
                        (c) -> {
                        	c.aHasManuallyInput = true;
                        	return c;
                        }, 
                        Channel.out("manualInput")
                        ,false);
        
        Behavior<Configuration> s2s =
                new Behavior<>(
                         "s2s",
                        (c) -> !c.aHasCorruptedSensor,
                        (c) -> {
                        	c.aHasCorruptedSensor = true;
                        	return c;
                        }, 
                        Channel.out("corruptSensor")
                        ,false);
        
        return Arrays.asList(z2o,o2t,t2t,s2s);
	}
	
	List<Behavior<Configuration>> manualValve() {

        Behavior<Configuration> o2c =
                new Behavior<>(
                         "MV_o2c",
                        (c) -> c.mIsOpen,
                        (c) -> {
                        	c.mIsOpen = false;
                        	return c;	
                        }, 
                        Channel.in("manualInput")
                        ,false);

        Behavior<Configuration> c2c =
                new Behavior<>(
                         "MV_c2c",
                        (c) -> !c.mIsOpen,
                        (c) -> c, 
                        Channel.in("flow")
                        ,false);
        

        Behavior<Configuration> c2o =
                new Behavior<>(
                         "MV_c2o",
                        (c) -> !c.mIsOpen,
                        (c) -> {
                        	c.mIsOpen = true;
                        	return c;
                        }, 
                        Channel.in("manualInput")
                        ,false);        

        Behavior<Configuration> o2u =
                new Behavior<>(
                         "MV_o2u",
                        (c) -> c.mIsOpen,
                        (c) -> {
                        	c.mTriggerDecrease = true;
                        	return c;
                        }, 
                        Channel.in("flow")
                        ,false);

        Behavior<Configuration> u2o =
                new Behavior<>(
                         "MV_u2o",
                        (c) -> c.mTriggerDecrease,
                        (c) -> {
                        	c.mTriggerDecrease = false;
                        	return c;
                        }, 
                        Channel.out("decrease")
                        ,true);
        
        return Arrays.asList(o2c,c2c,c2o,o2u,u2o);
	}
	
	List<Behavior<Configuration>> pump() {
		
        Behavior<Configuration> o2crit =
                new Behavior<>(
                         "Pump.o2crit",
                        (c) -> c.pIsOpen && !c.flagPump,
                        (c) -> {
                        	c.flagPump=true;
                        	return c;
                        }
                        ,true);	

        Behavior<Configuration> flow =
                new Behavior<>(
                         "Pump.flow",
                        (c) -> c.pIsOpen && (!c.flagIV || c.turn==c.TURN_PUMP),
                        (c) -> {
                        	c.flagPump=false;
                        	c.turn=c.TURN_IV;
                        	return c;
                        },  
                        Channel.out("flow")
                        ,false);        
        
        Behavior<Configuration> o2c =
                new Behavior<>(
                        "Pump.o2c",
                        (c) ->    c.pIsOpen,
                        (c) -> {
                            c.pIsOpen = false;
                            c.flagPump=false;
                            return c;
                        },
                        Channel.in("commandPumpOff")
                        );
        
        Behavior<Configuration> c2c =
                new Behavior<>(
                        "Pump.c2c",
                        (c) ->    !c.pIsOpen,
                        (c) -> {
                            return c;
                        },
                        Channel.in("commandPumpOff")
                        );
        
        Behavior<Configuration> c2o =
                new Behavior<>(
                         "Pump.c2o",
                        (c) -> !c.pIsOpen,
                        (c) -> {
                            c.pIsOpen = true;                            
                            return c;
						}, 
                        Channel.in("commandPumpOn")							 
                        );
        
        Behavior<Configuration> o2o =
                new Behavior<>(
                        "Pump.o2o",
                        (c) ->    c.pIsOpen,
                        (c) -> {
                            return c;
                        },
                        Channel.in("commandPumpOn")
                        );
        
        return Arrays.asList(o2crit, flow, o2o, o2c, c2o, c2c);
	}
	
	List<Behavior<Configuration>> sensor() {

        Behavior<Configuration> f2c =
                new Behavior<>(
                         "f2c",
                        (c) -> true,
                        (c) -> {
                            c.sWaterLevel = c.wtWaterLevel;
                            c.sTriggerSensor = true;
                            return c;
						},  
                        Channel.in("measure")
                        ,false);
        Behavior<Configuration> c2f =
                new Behavior<>(
                         "c2f",
                         (c) -> c.sTriggerSensor && !c.sIsCorrupted,
                         (c) -> {
                             c.sTriggerSensor = false;                            
                             return c;
 						},   
                        Channel.out("updateLevel"),
                        true);
        Behavior<Configuration> c2fa =
                new Behavior<>(
                         "c2fattacked",
                         (c) -> c.sTriggerSensor && c.sIsCorrupted,
                         (c) -> {
                             c.sTriggerSensor = false;                            
                             return c;
 						},
                         true);
        Behavior<Configuration> a2a =
                new Behavior<>(
                         "attacked",
                        (c) -> true,
                        (c) -> {
                        	c.sIsCorrupted = true;
                        	return c;
                        }, 
                        Channel.in("corruptSensor")
                        ,false);
        return Arrays.asList(f2c,c2f,c2fa,a2a);
	}
	
	 public BehaviorSoup<Configuration> model() {
         BehaviorSoup<Configuration> soup = new BehaviorSoup<>(new Configuration());

         List<Behavior<Configuration>> pump = pump();
         List<Behavior<Configuration>> sensor = sensor();
         List<Behavior<Configuration>> manualValve = manualValve();
         List<Behavior<Configuration>> attacker = attacker();
         List<Behavior<Configuration>> inflowValve = inflowValve();
         List<Behavior<Configuration>> scada = scada();
         List<Behavior<Configuration>> plc = plc();
         List<Behavior<Configuration>> waterTank = waterTank();

         soup.behaviors.addAll(pump);
         soup.behaviors.addAll(sensor);
         soup.behaviors.addAll(manualValve);
         soup.behaviors.addAll(attacker);
         soup.behaviors.addAll(inflowValve);
         soup.behaviors.addAll(scada);
         soup.behaviors.addAll(plc);
         soup.behaviors.addAll(waterTank);
         
         return soup;
     }
}
