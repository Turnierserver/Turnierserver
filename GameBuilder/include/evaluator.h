/*
 * evaluator.h
 * 
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 * 
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 * 
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#ifndef EVALUATOR_H
#define EVALUATOR_H

#include <QList>
#include <QString>

class BuildInstructions;
class LangSpec;

class Evaluator
{
	Q_DISABLE_COPY(Evaluator)
	
public:
	Evaluator(const BuildInstructions &instructions);
	~Evaluator();
	
	BuildInstructions instructions () const { return _instructions; }
	
	bool createLangSpecs(bool verbose = false);
	int target(const QString &target);
	
private:
	int target(const QString &target, LangSpec *spec);
	
	BuildInstructions _instructions;
	QList<LangSpec*> langSpecs;
	
};

#endif // EVALUATOR_H