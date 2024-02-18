export type Block = {
    componentCode: string;
    componentDescription: string;
    locationCode: string;
    roomCode: string;
    buildingCode: string;
    room: string;
    section: string;
    classNumber: number;
    classAssociation: number;
    instructionModeCode: string;
    instructionModeDescription: string;
    meetingPatternNumber: string;
    mondays: string;
    tuesdays: string;
    wednesdays: string;
    thursdays: string;
    fridays: string;
    saturdays: string;
    sundays: string;
    classStartTime: string;
    classEndTime: string;
    classStartDate: string;
    classEndDate: string;
};


export type Schedule = {
    blocks: Block[];
    term: string;
};
