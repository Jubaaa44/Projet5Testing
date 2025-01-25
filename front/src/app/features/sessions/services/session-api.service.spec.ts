import { HttpClientModule, HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';

describe('SessionApiService', () => {
 let service: SessionApiService;
 let httpClient: HttpClient;

 const mockSession: Session = {
   id: 1,
   name: 'Test Session',
   description: 'Test Description',
   date: new Date(),
   teacher_id: 1,
   users: [1, 2],
   createdAt: new Date(),
   updatedAt: new Date()
 };

 beforeEach(() => {
   TestBed.configureTestingModule({
     imports: [HttpClientModule]
   });
   service = TestBed.inject(SessionApiService);
   httpClient = TestBed.inject(HttpClient);
 });

 it('should be created', () => {
   expect(service).toBeTruthy();
 });

 it('should get all sessions', () => {
   const spy = jest.spyOn(httpClient, 'get').mockReturnValue(of([mockSession]));
   
   service.all().subscribe(sessions => {
     expect(sessions).toEqual([mockSession]);
   });
   
   expect(spy).toHaveBeenCalledWith('api/session');
 });

 it('should get session detail', () => {
   const spy = jest.spyOn(httpClient, 'get').mockReturnValue(of(mockSession));
   
   service.detail('1').subscribe(session => {
     expect(session).toEqual(mockSession);
   });
   
   expect(spy).toHaveBeenCalledWith('api/session/1');
 });

 it('should delete session', () => {
   const spy = jest.spyOn(httpClient, 'delete').mockReturnValue(of({}));
   
   service.delete('1').subscribe();
   
   expect(spy).toHaveBeenCalledWith('api/session/1');
 });

 it('should create session', () => {
   const spy = jest.spyOn(httpClient, 'post').mockReturnValue(of(mockSession));
   
   service.create(mockSession).subscribe(session => {
     expect(session).toEqual(mockSession);
   });
   
   expect(spy).toHaveBeenCalledWith('api/session', mockSession);
 });

 it('should update session', () => {
   const spy = jest.spyOn(httpClient, 'put').mockReturnValue(of(mockSession));
   
   service.update('1', mockSession).subscribe(session => {
     expect(session).toEqual(mockSession);
   });
   
   expect(spy).toHaveBeenCalledWith('api/session/1', mockSession);
 });

 it('should handle session participation', () => {
   const spy = jest.spyOn(httpClient, 'post').mockReturnValue(of(void 0));
   
   service.participate('1', '2').subscribe();
   
   expect(spy).toHaveBeenCalledWith('api/session/1/participate/2', null);
 });

 it('should handle session unparticipation', () => {
   const spy = jest.spyOn(httpClient, 'delete').mockReturnValue(of(void 0));
   
   service.unParticipate('1', '2').subscribe();
   
   expect(spy).toHaveBeenCalledWith('api/session/1/participate/2');
 });
});