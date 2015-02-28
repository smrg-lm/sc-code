// Pseudo UGen for routing

FadeOut : AbstractOut {
	*ar { arg outBus = 0, signal = 0.0, maxFadeTime = 0.02, fadeTime = 0.02;
		var changed, newOut, oldOut, initTrig;

		outBus.asArray.do({ arg outBus;
			initTrig = Impulse.kr(0);
			outBus = K2A.ar(outBus);
			changed = HPZ1.ar(outBus).abs;
			newOut = Delay1.ar(outBus);
			oldOut = DelayN.ar(outBus, maxFadeTime, fadeTime);

			OffsetOut.ar(newOut,
				signal * EnvGen.ar(Env([0, 0, 1], [0, fadeTime], \lin),
				gate: changed + initTrig));
			OffsetOut.ar(oldOut,
				signal * EnvGen.ar(Env([0, 1, 0], [0, fadeTime], \lin),
				gate: changed));
		});

		^0.0;
	}

	*kr { arg outBus = 0, signal = 0.0, maxFadeTime = 0.02, fadeTime = 0.02;
		var changed, newOut, oldOut, initTrig;

		outBus.asArray.do({ arg outBus;
			initTrig = Impulse.kr(0);
			outBus = A2K.kr(outBus);
			changed = HPZ1.kr(outBus).abs;
			newOut = Delay1.kr(outBus);
			oldOut = DelayN.kr(outBus, maxFadeTime, fadeTime);

			Out.kr(newOut,
				signal * EnvGen.kr(Env([0, 0, 1], [0, fadeTime], \lin),
				gate: changed + initTrig));
			Out.kr(oldOut,
				signal * EnvGen.kr(Env([0, 1, 0], [0, fadeTime], \lin),
				gate: changed));
		});

		^0.0;
	}
}

/*
// sine test
// Out
x = { arg out = 0; Out.ar(out, SinOsc.ar(mul: 0.1)) }.play;

x.set(\out, 1);
x.set(\out, 4);
x.set(\out, 2);
x.set(\out, 5);
x.set(\out, 3);
x.set(\out, 7);
x.free;

// FadeOut delayTime = 1
x = { arg out = 0; FadeOut.ar(out, SinOsc.ar(mul: 0.2), 1, 1) }.play;

x.set(\out, 1);
x.set(\out, 4);
x.set(\out, 2);
x.set(\out, 3);
x.set(\out, 0);
x.set(\out, 7);
x.free;

////////////////
// old example

(
SynthDef(\src, { arg out, i_maxFadeTime = 3, fadeTime = 0.5;
	FadeOut.ar(
		out,
		SinOsc.ar(
			Demand.kr(Impulse.kr(3), 0, Drand([48, 62, 59, 76].midicps, inf)).lag(0.1),
			mul: LFNoise1.kr(0.1).range(0.05, 0.4)
		),
		i_maxFadeTime,
		fadeTime
	);
}).add;

SynthDef(\mod, { arg out = 0, in;
	Out.ar(
		out,
		In.ar(in) * SinOsc.ar([20, 30])
	)
}).add;

SynthDef(\rev, { arg out = 0, in;
	Out.ar(
		out,
		GVerb.ar(In.ar(in)) * 0.25
	);
}).add;

SynthDef(\ver, { arg out = 0, in;
	var freq, hasFreq, amp;
	# freq, hasFreq = Pitch.kr(In.ar(in));
	amp = Amplitude.ar(In.ar(in));
	Out.ar(
		out,
		SinOsc.ar(440 + SinOsc.ar(freq, mul:100, add:100)).dup * amp
	);
}).add;
)

(
f = fork {
	var ft;
	b = Bus.audio(s, 1);
	c = Bus.audio(s, 1);
	d = Bus.audio(s, 1);
	w = Synth(\mod, [in: b]);
	x = Synth(\rev, [in: c]);
	y = Synth(\ver, [in: d]);
	z = Synth(\src, [out: 0]);
	3.wait;
	loop {
		ft = rrand(0.1, 2).postln; // post the fade time
		z.set(\out, [0, 1, b, c, d].choose, \fadeTime, ft);
		(ft + 2).wait;
	}
};
)

(
f.stop;
[w, x, y, z].do(_.free);
)

///////////////
// kr example

a = Bus.control(s, 1);
b = Bus.control(s, 1);

a.set(0);
b.set(0);

x = { arg out; FadeOut.kr(out, LFNoise2.kr(25, 220, 220), 1, 1) }.play;
x.set(\out, a);
y = { arg ctrIn; Out.ar(0, SinOsc.ar(440 + In.kr(ctrIn))) }.play;
y.set(\ctrIn, a);
z = { arg ctrIn; Out.ar(1, Saw.ar(440 + In.kr(ctrIn))) }.play;
z.set(\ctrIn, b);

(
f = fork {
	loop {
		x.set(\out, b);
		2.wait;
		x.set(\out, a);
		2.wait;
	}
}
)

f.stop;
[a, b, x, y, z].do(_.free);

*/