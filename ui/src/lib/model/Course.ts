import type { Instructor } from './Instructor';
import type { ReqNode } from './Requirements';
import type { Schedule } from './Schedule';

export type Course = {
  _id: string;
  title: string;
  classUnit: string; //credits
  subject: string;
  catalog: string;
  career: string;
  url: string;
  department: string;
  faculty: string;
  facultyUrl: string;
  terms: string[];
  description: string;
  instructors: Instructor[];
  prerequisitesText?: string;
  corequisitesText?: string;
  prerequisites: string[];
  corequisites: string[];
  logicalPrerequisites?: ReqNode;
  logicalCorequisites?: ReqNode;
  leadingTo: string[];
  restrictions: string;
  schedule: Schedule[];
};
